package org.jetbrains.sbtidea.download.idea

import java.net.URL
import java.nio.file.{Files, Path}

import org.jetbrains.sbtidea.download.{FileDownloader, NioUtils}
import org.jetbrains.sbtidea.{PluginLogger => log}
import org.jetbrains.sbtidea.download.api._

case class IdeaDist(caller: IdeaDependency, baseDirectory: Path, dlUrl: URL) extends IdeaArtifact {

  override type R = IdeaDist
  override protected def usedInstaller: Installer[IdeaDist] = new Installer[IdeaDist] {

    override def isInstalled(art: IdeaDist): Boolean =
      baseDirectory.toFile.exists() && baseDirectory.toFile.listFiles().nonEmpty

    override def downloadAndInstall(art: IdeaDist): Unit =
      installDist(downloader.download(dlUrl))

    private val downloader  = new FileDownloader(baseDirectory.getParent)
    private val buildInfo   = caller.buildInfo
    private val tmpDir      = baseDirectory.getParent.resolve(s"${buildInfo.edition.name}-${buildInfo.buildNumber}-TMP")

    private def installDist(artifact: Path): Unit = {
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
          Files.move(actualDir, baseDirectory)
          Files.deleteIfExists(tmpDir)
        case _ =>
          Files.move(tmpDir, baseDirectory)
      }

      NioUtils.delete(artifact)
      log.info(s"Installed ${buildInfo.edition.name}($buildInfo) to $baseDirectory")
    }
  }
}
