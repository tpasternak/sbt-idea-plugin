package org.jetbrains.sbtidea.download.api

import java.net.URL
import java.nio.file.Path

import org.jetbrains.sbtidea.download.FileDownloader

trait UnresolvedArtifact {
  type U >: this.type <: UnresolvedArtifact
  type R <: ResolvedArtifact

  protected def usedResolver: Resolver[U]

  def resolve: Seq[this.R] = usedResolver.resolve(this)
}

trait ResolvedArtifact {
  type R >: this.type <: ResolvedArtifact

  protected def usedInstaller: Installer[R]

  def isInstalled(implicit ctx: InstallContext): Boolean   = usedInstaller.isInstalled(this)
  def install    (implicit ctx: InstallContext): Unit      = usedInstaller.downloadAndInstall(this)
}

trait Resolver[U <: UnresolvedArtifact] {
  def resolve(dep: U): Seq[dep.R]
}

trait Installer[R <: ResolvedArtifact] {
  def isInstalled(art: R)(implicit ctx: InstallContext): Boolean
  def downloadAndInstall(art: R)(implicit ctx: InstallContext): Unit
}

// ================

trait BaseDirectoryAware {
  def baseDirectory: Path
}

trait UrlBasedArtifact {
  def dlUrl: URL
}