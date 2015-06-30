resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.6"
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
