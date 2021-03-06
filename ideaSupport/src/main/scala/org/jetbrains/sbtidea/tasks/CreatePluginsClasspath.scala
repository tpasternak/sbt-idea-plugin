package org.jetbrains.sbtidea.tasks

import sbt._
import Keys._
import org.jetbrains.sbtidea.Keys._
import org.jetbrains.sbtidea.PluginLogger
import org.jetbrains.sbtidea.download.LocalPluginRegistry
import org.jetbrains.sbtidea.download.LocalPluginRegistry._


object CreatePluginsClasspath {

  def apply(pluginsBase: File, pluginsUsed: Seq[String], externalPlugins: Seq[IntellijPlugin], log: PluginLogger): Classpath = {
    val localRegistry = new LocalPluginRegistry(pluginsBase.getParentFile.toPath, log)
    val externalPluginsFinder = externalPlugins
      .map(localRegistry.getInstalledPluginRoot)
      .map(_.toFile)
      .foldLeft(PathFinder.empty) { (pathFinder, pluginRoot) =>
        if (pluginRoot.isDirectory)
          pathFinder +++ ((pluginRoot / "lib") * (globFilter("*.jar") -- "asm*.jar"))
        else
          pathFinder +++pluginRoot
    }

    val bundledPluginsFinder = pluginsUsed
      .foldLeft(PathFinder.empty) { (paths, plugin) =>
        if ((pluginsBase / s"$plugin.jar").exists())
          paths +++ (pluginsBase / s"$plugin.jar")
        else if ((pluginsBase / plugin / "lib").exists())
          paths +++ ((pluginsBase / plugin / "lib") * (globFilter("*.jar") -- "asm*.jar"))
        else
          throw new MissingPluginRootException(plugin)
      }
    val pluginsFinder = bundledPluginsFinder +++ externalPluginsFinder
    pluginsFinder.classpath
  }

}
