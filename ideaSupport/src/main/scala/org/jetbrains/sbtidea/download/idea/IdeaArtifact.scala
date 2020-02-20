package org.jetbrains.sbtidea.download.idea

import org.jetbrains.sbtidea.download.api._

abstract class IdeaArtifact extends ResolvedArtifact with BaseDirectoryAware with UrlBasedArtifact {
  def caller: IdeaDependency
}


