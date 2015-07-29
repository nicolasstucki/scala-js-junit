# Scala.js JUnit integration plugin
JUnit testing framework integration into SBT for Scala.JS.

## How to use
1. Clone this project
2. Publish locally
  1. Open `sbt` in your local copy the project
  2. `> ++2.11.7`
  3. `> ;scalajs-junit-plugin/publishLocal;scalajs-junit/publishLocal`
3. Configure your `build.sbt` file
  1. Add library dependencies:
    ```scala
    libraryDependencies ++= Seq(
      "scala-js-junit" % "scala-js-junit_sjs0.6_2.11" % "0.0.1-SNAPSHOT" % "test",
      "scala-js-junit-plugin" % "scala-js-junit-plugin_2.11" % "0.0.1-SNAPSHOT" % "plugin")
    ```
  2. Set testing framework: 
    ```scala
    testFrameworks += new TestFramework("org.scalajs.junit.JUnitFramework")
    ```
  3. Add testing arguments (optional):
    ```scala
    testOptions += Tests.Argument(new TestFramework("org.scalajs.junit.JUnitFramework"), "-v" /*, "-q", "-n", "-a", "-c" */)
    ```
4. Run your JUnit tests with `test` or `testOnly`.
