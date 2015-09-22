# Scala.js JUnit integration plugin
JUnit testing framework integration into SBT for Scala.JS.

[Example project](https://github.com/nicolasstucki/scala-js-junit-examples)

## How to use
* Clone this project
* Publish locally
  1. Open `sbt` in your local copy the project
  2. `> ++2.11.7`
  3. `> ;scalajs-junit-plugin/publishLocal;scalajs-junit/publishLocal`
* Configure your `build.sbt` file

* Add library dependencies:
```scala
libraryDependencies ++= Seq(
  "scala-js-junit" % "scala-js-junit_sjs0.6_2.11" % "0.0.1-SNAPSHOT" % "test",
  "scala-js-junit-plugin" % "scala-js-junit-plugin_2.11" % "0.0.1-SNAPSHOT" % "plugin")
```
* Set testing framework: 
```scala
testFrameworks += new TestFramework("org.scalajs.junit.JUnitFramework")
```
* Add testing arguments (optional):
```scala
testOptions += Tests.Argument(new TestFramework("org.scalajs.junit.JUnitFramework"), 
    "-v" /*, "-q", "-n", "-a", "-c" */)
```
* Run your JUnit tests with `test` or `testOnly`.
