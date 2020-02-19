package org.jetbrains.sbtidea.download

import org.jetbrains.sbtidea.download.api._

class CommunityUpdater {
  def dependencies: Seq[UnresolvedArtifact] = ???
  val x: UnresolvedArtifact = ???
  val c = x.resolve.foreach(a => a.install())
//  x.usedInstaller.isInstalled(x)
//  x.usedResolver.resolve(x)
}
