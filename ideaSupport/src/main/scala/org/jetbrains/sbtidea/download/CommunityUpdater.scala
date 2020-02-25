package org.jetbrains.sbtidea.download

import java.nio.file.Path

import org.jetbrains.sbtidea.download.api._
import org.jetbrains.sbtidea.download.idea.{IdeaDependency, IdeaDist}
import org.jetbrains.sbtidea.download.plugin.PluginDependency
import org.jetbrains.sbtidea.Keys.IntellijPlugin

class CommunityUpdater(baseDirectory: Path, ideaBuildInfo: BuildInfo, plugins: Seq[IntellijPlugin], withSources: Boolean = true) {
  implicit val context: InstallContext = InstallContext(baseDirectory)

  def dependencies: Seq[UnresolvedArtifact] =

  def update(): Unit = {
  }
}
