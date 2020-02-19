package org.jetbrains.sbtidea.download.api

case class PluginDependency(plugin: org.jetbrains.sbtidea.Keys.IntellijPlugin) extends UnresolvedArtifact {
  override type U = PluginDependency
  override type R = PluginArtifact
  override protected def usedResolver: Resolver[PluginDependency] = new PluginResolver
}

class PluginArtifact extends ResolvedArtifact {
  override type R = PluginArtifact
  override def usedInstaller: Installer[PluginArtifact] = new PluginInstaller
}

class PluginResolver extends Resolver[PluginDependency] {
  override def resolve(art: PluginDependency): Seq[PluginArtifact] = ???
}

class PluginInstaller extends Installer[PluginArtifact] {
  override def isInstalled(art: PluginArtifact): Boolean = ???
  override def downloadAndInstall(art: PluginArtifact): Unit = ???
}

