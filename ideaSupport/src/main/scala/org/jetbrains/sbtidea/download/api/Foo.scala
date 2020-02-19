package org.jetbrains.sbtidea.download.api

import java.net.URL
import java.nio.file.Path

trait UnresolvedArtifact {
  type U >: this.type <: UnresolvedArtifact
  type R <: ResolvedArtifact

  protected def usedResolver: Resolver[U]

  def resolve: Seq[this.R] = usedResolver.resolve(this)
}

trait ResolvedArtifact {
  type R >: this.type <: ResolvedArtifact

  protected def usedInstaller: Installer[R]

  def isInstalled: Boolean   = usedInstaller.isInstalled(this)
  def install(): Unit        = usedInstaller.downloadAndInstall(this)
}

trait Resolver[U <: UnresolvedArtifact] {
  def resolve(art: U): Seq[art.R]
}

trait Installer[R <: ResolvedArtifact] {
  def isInstalled(art: R): Boolean
  def downloadAndInstall(art: R): Unit
}

trait BaseDirectoryAware {
  def baseDirectory: Path
}

abstract class UrlBasedArtifact extends ResolvedArtifact with BaseDirectoryAware {
  override type R = UrlBasedArtifact
  override protected def usedInstaller: UrlInstaller.type = UrlInstaller
  def url: URL
}

object UrlInstaller extends Installer[UrlBasedArtifact] {
  override def isInstalled(art: UrlBasedArtifact): Boolean = ???
  override def downloadAndInstall(art: UrlBasedArtifact): Unit = ???

  private def download(art: UrlBasedArtifact) = ???
}

case class FooArtifact(override val url: URL, override val baseDirectory: Path) extends UrlBasedArtifact

object Foo {
//  FooArtifact(???).install()
}