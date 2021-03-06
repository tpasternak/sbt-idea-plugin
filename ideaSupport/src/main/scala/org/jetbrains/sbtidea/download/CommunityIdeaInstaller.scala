package org.jetbrains.sbtidea.download

import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Path}
import java.util.function.Consumer

import org.jetbrains.sbtidea.PluginLogger
import org.jetbrains.sbtidea.download.api.IdeaInstaller

abstract class CommunityIdeaInstaller(ideaInstallDir: Path,
                             override val buildInfo: BuildInfo,
                             override val log: PluginLogger) extends IdeaInstaller {

  override def getInstallDir: Path = ideaInstallDir

  override def isIdeaAlreadyInstalled: Boolean = getInstallDir.toFile.exists() && getInstallDir.toFile.listFiles().nonEmpty

  protected def tmpDir: Path = getInstallDir.getParent.resolve(s"${buildInfo.edition.name}-${buildInfo.buildNumber}-TMP")

  override def installIdeaDist(files: Seq[(ArtifactPart, Path)]): Path = {
    val dist = files
      .collectFirst { case (ArtifactPart(_, ArtifactKind.IDEA_DIST, _, _), file) => file }
      .getOrElse(throw new RuntimeException(s"Can't install ${buildInfo.edition.name}: distribution is missing: $files"))
    val src = files
      .collectFirst { case (ArtifactPart(_, ArtifactKind.IDEA_SRC, _, _), file) if file.toFile.exists() => file }
    val extras = files
      .collect({ case a@(ArtifactPart(_, ArtifactKind.MISC, _, _), _) => a })

    ensureFolderStructure()

    installDist(dist)
    installExtras(extras)
    if (src.nonEmpty)
      installSources(src.head)
    else log.warn(s"No ${buildInfo.edition.name} sources have been downloaded")

    fixAccessRights()

    getInstallDir
  }

  protected def installDist(artifact: Path): Unit = {
    import sys.process._
    import org.jetbrains.sbtidea.Keys.IntelliJPlatform.MPS

    log.info(s"Extracting ${buildInfo.edition.name} dist to $tmpDir")

    if (artifact.getFileName.toString.endsWith(".zip")) {
      sbt.IO.unzip(artifact.toFile, tmpDir.toFile)
    } else if (artifact.getFileName.toString.endsWith(".tar.gz")) {
      if (s"tar xfz $artifact -C $tmpDir --strip 1".! != 0) {
        throw new RuntimeException(s"Failed to install ${buildInfo.edition.name} dist: tar command failed")
      }
    } else throw new RuntimeException(s"Unexpected dist archive format(not zip or gzip): $artifact")

    buildInfo.edition match {
      case MPS if Files.list(tmpDir).count() == 1 => // MPS may add additional folder level to the artifact
        log.info("MPS detected: applying install dir quirks")
        val actualDir = Files.list(tmpDir).iterator().next()
        Files.move(actualDir, getInstallDir)
        Files.deleteIfExists(tmpDir)
      case _ =>
        Files.move(tmpDir, getInstallDir)
    }

    NioUtils.delete(artifact)
    log.info(s"Installed ${buildInfo.edition.name}($buildInfo) to $getInstallDir")
  }

  protected def installSources(artifact: Path): Unit = {
    Files.move(artifact, getInstallDir.resolve("sources.zip"))
    log.info(s"${buildInfo.edition.name} sources installed")
  }

  protected def installExtras(files: Seq[(ArtifactPart, Path)]): Unit = ()

  private def fixAccessRights(): Unit = {
    if (!System.getProperty("os.name").startsWith("Windows")) {
      val execPerms = PosixFilePermissions.fromString("rwxrwxr-x")
      val binDir    = getInstallDir.resolve("bin")
      try {
        Files
          .walk(binDir)
          .forEach(new Consumer[Path] {
            override def accept(t: Path): Unit =
              Files.setPosixFilePermissions(t, execPerms)
          })
      } catch {
        case e: Exception => log.warn(s"Failed to fix access rights for $binDir: ${e.getMessage}")
      }
    }
  }

  private def ensureFolderStructure(): Unit = {
    getInstallDir.toFile.getParentFile.mkdirs() // ensure "sdk" directory exists
    NioUtils.delete(getInstallDir)
    NioUtils.delete(tmpDir)
    Files.createDirectories(tmpDir)
  }

}
