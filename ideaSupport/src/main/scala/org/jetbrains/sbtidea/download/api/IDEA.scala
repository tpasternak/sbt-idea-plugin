package org.jetbrains.sbtidea.download.api

class IdeaDependency extends UnresolvedArtifact {
  override type U = IdeaDependency
  override type R = IdeaArtifact

  override protected def usedResolver: IJRepoIdeaResolver = ???
}

class IdeaArtifact extends ResolvedArtifact {
  override type R = IdeaArtifact

  override protected def usedInstaller: IdeaInstaller.type = ???
}

class IJRepoIdeaResolver extends Resolver[IdeaDependency] {
  override def resolve(art: IdeaDependency): Seq[IdeaArtifact] = ???
}

object IdeaInstaller extends Installer[IdeaArtifact] {
  override def isInstalled(art: IdeaArtifact): Boolean = ???
  override def downloadAndInstall(art: IdeaArtifact): Unit = ???
}