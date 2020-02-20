package org.jetbrains.sbtidea.download.idea

import java.net.URL
import java.nio.file.{Files, Path}

import org.jetbrains.sbtidea.download.FileDownloader
import org.jetbrains.sbtidea.{PluginLogger, pathToPathExt}
import sbt._
import org.jetbrains.sbtidea.download.api._

import scala.language.postfixOps

case class IdeaSources(caller: IdeaDependency, baseDirectory: Path, dlUrl: URL) extends IdeaArtifact {
  override type R = IdeaSources
  override protected def usedInstaller: Installer[IdeaSources] = new Installer[IdeaSources] {
    override def isInstalled(art: IdeaSources): Boolean = baseDirectory / "sources.zip" exists
    override def downloadAndInstall(art: IdeaSources): Unit = {
      val file = FileDownloader(baseDirectory.getParent).download(dlUrl, optional = true)
      Files.move(file, baseDirectory.resolve("sources.zip"))
      PluginLogger.info(s"${caller.buildInfo.edition.name} sources installed")
    }
  }
}
