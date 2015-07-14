package org.scalajs.junit

import sbt.testing._

final class JUnitFramework extends Framework {

  val name: String = "Scala.JS JUnit test framework"

  private object JUnitFingerprint extends AnnotatedFingerprint {
    override def annotationName(): String = "org.junit.Test"

    override def isModule(): Boolean = false

    override def equals(obj: Any): Boolean = {
      obj match {
        case obj: AnnotatedFingerprint =>
          annotationName() == obj.annotationName() && isModule() == obj.isModule()
        case _ => false
      }
    }
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
