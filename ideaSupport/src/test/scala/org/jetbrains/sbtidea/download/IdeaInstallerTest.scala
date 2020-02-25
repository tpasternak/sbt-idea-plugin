package org.jetbrains.sbtidea.download

import java.nio.file.Path

import org.jetbrains.sbtidea.download.api.InstallContext
import org.jetbrains.sbtidea.download.idea.{IdeaDependency, IdeaDist, IdeaDistInstaller}
import org.jetbrains.sbtidea.{ConsoleLogger, Keys, TmpDirUtils}
import org.scalatest.{FunSuite, Matchers}
import org.jetbrains.sbtidea.pathToPathExt

class IdeaInstallerTest extends FunSuite with Matchers with IdeaMock with TmpDirUtils with ConsoleLogger {

  private def createInstaller = new IdeaDistInstaller(IDEA_BUILDINFO)
  private implicit val installContext: InstallContext = {
    val tmpDir = newTmpDir
    InstallContext(tmpDir, tmpDir)
  }

  test("IdeaInstaller installs IDEA dist") {
    val installer = createInstaller
    val dist = getDistCopy
    val ideaInstallRoot = installer.installDist(dist)
    ideaInstallRoot.toFile.exists() shouldBe true
    ideaInstallRoot.list.map(_.getFileName.toString) should contain allElementsOf Seq("lib", "bin", "plugins", "build.txt")
  }

}
