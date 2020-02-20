package org.jetbrains.sbtidea.download.plugin

import java.nio.file.Path

import org.jetbrains.sbtidea.Keys.IntellijPlugin
import org.jetbrains.sbtidea.download.BuildInfo
import org.jetbrains.sbtidea.download.api._


case class PluginDependency(plugin: IntellijPlugin, baseDirectory: Path, buildInfo: BuildInfo)
  extends UnresolvedArtifact with BaseDirectoryAware {
  override type U = PluginDependency
  override type R = PluginArtifact
  override protected def usedResolver: PluginResolver.type = PluginResolver
}