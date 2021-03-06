package org.jetbrains.sbtidea.packaging.structure.sbtImpl

import java.io.File

import org.jetbrains.sbtidea.packaging.PackagingKeys.ExcludeFilter.ExcludeFilter
import org.jetbrains.sbtidea.packaging.PackagingKeys.ShadePattern
import org.jetbrains.sbtidea.packaging.structure.{PackagedProjectNode, PackagingMethod, ProjectPackagingOptions}
import org.jetbrains.sbtidea.structure.ModuleKey

case class SbtProjectPackagingOptionsImpl(override val packageMethod: PackagingMethod,
                                          override val libraryMappings: Seq[(ModuleKey, Option[String])],
                                          override val fileMappings: Seq[(File, String)],
                                          override val shadePatterns: Seq[ShadePattern],
                                          override val excludeFilter: ExcludeFilter,
                                          override val classRoots: Seq[File],
                                          override val assembleLibraries: Boolean,
                                          override val additionalProjects: Seq[PackagedProjectNode]) extends ProjectPackagingOptions