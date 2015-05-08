package org.scalajs.junit

import sbt.testing._

final class JUnitFramework extends Framework {

  val name: String = "Scala.JS JUnit test framework"

  private object JUnitFingerprint extends SubclassFingerprint {
    val isModule: Boolean = false
    val superclassName: String = "org.scalajs.junit.Test"
    val requireNoArgConstructor: Boolean = true
  }

  def fingerprints: Array[Fingerprint] = {
    Array(JUnitFingerprint)
  }

  def runner(args: Array[String], remoteArgs: Array[String],
    testClassLoader: ClassLoader): MasterRunner = {
    new MasterRunner(args, remoteArgs, testClassLoader)
  }

  def slaveRunner(args: Array[String], remoteArgs: Array[String],
    testClassLoader: ClassLoader, send: String => Unit): SlaveRunner = {
    new SlaveRunner(args, remoteArgs, testClassLoader, send)
  }

  def arrayString(arr: Array[String]): String = arr.mkString("Array(", ", ", ")")

}
