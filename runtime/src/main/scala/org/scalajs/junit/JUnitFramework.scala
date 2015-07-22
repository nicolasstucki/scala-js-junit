package org.scalajs.junit

import com.novocode.junit.RunSettings
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
    new MasterRunner(args, remoteArgs, testClassLoader, parseRunSettings(args))
  }

  def slaveRunner(args: Array[String], remoteArgs: Array[String],
    testClassLoader: ClassLoader, send: String => Unit): SlaveRunner = {
    new SlaveRunner(args, remoteArgs, testClassLoader, send, parseRunSettings(args))
  }

  def arrayString(arr: Array[String]): String = arr.mkString("Array(", ", ", ")")


  def parseRunSettings(args: Array[String]): RunSettings = {
    var quiet = false
    var verbose = false
    var nocolor = false
    var decodeScalaNames = false
    var logAssert = false
    var logExceptionClass = true
    var ignoreRunners = "org.junit.runners.Suite"
    var runListener: String = null
    for (s <- args) {
      if("-q".equals(s)) quiet = true
      else if("-v".equals(s)) verbose = true
      else if("-n".equals(s)) nocolor = true
      else if("-s".equals(s)) decodeScalaNames = true
      else if("-a".equals(s)) logAssert = true
      else if("-c".equals(s)) logExceptionClass = false
      else if(s.startsWith("-tests=")) throw new NotImplementedError("-tests")
      else if(s.startsWith("--tests=")) throw new NotImplementedError("--tests")
      else if(s.startsWith("--ignore-runners=")) ignoreRunners = s.substring(17)
      else if(s.startsWith("--run-listener=")) runListener = s.substring(15)
      else if(s.startsWith("--include-categories=")) throw new NotImplementedError("--include-categories")
      else if(s.startsWith("--exclude-categories=")) throw new NotImplementedError("--exclude-categories")
      else if(s.startsWith("-D") && s.contains("=")) throw new NotImplementedError("-Dkey=value")
      else if(!s.startsWith("-") && !s.startsWith("+")) throw new NotImplementedError("-/+")
    }
    for (s <- args) {
      s match {
        case "+q" => quiet = false
        case "+v" => verbose = false
        case "+n" => nocolor = false
        case "+s" => decodeScalaNames = false
        case "+a" => logAssert = false
        case "+c" => logExceptionClass = false
        case _ =>
      }
    }
    new RunSettings(!nocolor, decodeScalaNames, quiet, verbose, logAssert, ignoreRunners, logExceptionClass)
  }
}
