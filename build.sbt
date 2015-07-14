resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val `scalajs-junit` = project.in(file("runtime")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings: _*).
  settings(
    name := "Scala.js JUnit",
    libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % scalaJSVersion
  )

lazy val `scalajs-junit-plugin` = project.in(file("junit-plugin")).
  settings(commonSettings: _*).
  settings(
    name := "Scala.js JUnit plugin",
    libraryDependencies := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
        case Some((2, scalaMajor)) if scalaMajor >= 11 =>
          libraryDependencies.value
        // in Scala 2.10, quasiquotes are provided by macro paradise
        case Some((2, 10)) =>
          libraryDependencies.value ++ Seq(
            compilerPlugin("org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full),
            "org.scalamacros" %% "quasiquotes" % "2.0.0" cross CrossVersion.binary)
      }
    },
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    exportJars := true
  ).dependsOn(`scalajs-junit`)

lazy val testSuite = project.in(file("test-suite")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings: _*).
  settings(
    name := "Scala.js JUnit test",
    testFrameworks += new TestFramework("org.scalajs.junit.JUnitFramework")
  ).dependsOn(`scalajs-junit` % "test", `scalajs-junit-plugin` % "plugin")
