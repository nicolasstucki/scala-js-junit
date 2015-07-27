package org.scalajs.junit

import com.novocode.junit.Ansi._
import com.novocode.junit.{OutputCapture, RichLogger}
import org.junit._

import scala.util.{Failure, Success, Try}

final class JUnitExecuteTest(fullyQualifiedName: String,
    runner: JUnitBaseRunner, classMetadata: JUnitTestMetadata,
    richLogger: RichLogger) {

  def executeTests(): Unit = {
    val packageName = fullyQualifiedName.split('.').init.mkString(".")
    val className = fullyQualifiedName.split('.').last

    val jUnitMetadata = classMetadata.scalajs$junit$metadata

    Try {
      for (method <- jUnitMetadata.beforeClassMethod)
        classMetadata.scalajs$junit$invoke(method.id)
    } match {
      case Success(_) =>
        val outputCapture =
          if (runner.runSettings.quiet) OutputCapture.start()
          else null

        try {
          for (method <- jUnitMetadata.testMethods) {
            method.getIgnoreAnnotation match {
              case Some(ign) =>
                ignoreTest(packageName, className, method.name)

              case None =>
                executeTestMethod(classMetadata, packageName, className, method)
            }
          }
        } finally {
          if (outputCapture != null)
            outputCapture.stop()
        }

        for (method <- jUnitMetadata.afterClassMethod)
          classMetadata.scalajs$junit$invoke(method.id)

      case Failure(ex: AssumptionViolatedException[_]) =>
        logFormattedInfo(packageName, className, null, "ignored")
        runner.taskSkipped()

      case Failure(ex: internal.AssumptionViolatedException) =>
        logFormattedInfo(packageName, className, null, "ignored")
        runner.taskSkipped()

      case Failure(ex) => throw ex
    }
  }

  private[this] def executeTestMethod(classMetadata: JUnitTestMetadata,
      packageName: String, className: String, method: JUnitMethodMetadata) = {
    val jUnitMetadata = classMetadata.scalajs$junit$metadata
    val testClassInstance = classMetadata.scalajs$junit$newInstance

    val t0 = System.currentTimeMillis
    val beforeMethods = Try {
      for (method <- jUnitMetadata.beforeMethod)
        testClassInstance.scalajs$junit$invoke(method.id)
    }
    val testMethod = if (beforeMethods.isFailure) Failure(null) else {
       Try(testClassInstance.scalajs$junit$invoke(method.id) )
    }
    val afterMethods = if(testMethod.isFailure) Failure(null) else Try {
      for (method <- jUnitMetadata.afterMethod)
        testClassInstance.scalajs$junit$invoke(method.id)
    }
    val timeInSeconds = (System.currentTimeMillis - t0).toDouble / 1000

    val testAnnotation = method.getTestAnnotation().get

    beforeMethods match {
      case Success(_) =>
        testMethod match {
          case Success (_) =>
            executedWithoutExceptions(packageName, className, method.name,
                testAnnotation, timeInSeconds)

            afterMethods match {
              case Success (_) => // Do nothing
              case Failure(exception) => ???
            }

          case Failure(exception) =>
            executedWithExceptions(packageName, className, method.name,
                testAnnotation, timeInSeconds, exception)
        }

      case Failure(ex: AssumptionViolatedException[_]) =>
        logFormattedInfo(packageName, className, method.name, "started")
        logAssertionWarning(packageName, className, method.name, ex,
            timeInSeconds)

      case Failure(ex: internal.AssumptionViolatedException) =>
        logFormattedInfo(packageName, className, method.name, "started")
        logAssertionWarning(packageName, className, method.name, ex,
            timeInSeconds)

      case Failure(ex) => throw ex
    }

//    if (testAnnotation.timeout != 0 && testAnnotation.timeout <= time) {
//      richLogger.warn ("Timeout: took " + timeInSeconds + " sec, expected " +
//        (testAnnotation.timeout.toDouble / 1000) + " sec")
//    }

  }

  private[this] def ignoreTest(packageName: String, className: String,
      methodName: String) = {
    logFormattedInfo(packageName, className, methodName, "ignored")
    runner.taskSkipped()
    runner.taskRegisterTotal()
  }

  private[this] def executedWithoutExceptions(packageName: String,
      className: String, methodName: String, testAnnotation: org.junit.Test,
      timeInSeconds: Double) = {
    if (testAnnotation.expected == classOf[org.junit.Test.None]) {
      if (runner.runSettings.verbose)
        logFormattedInfo(packageName, className, methodName, "started")
      runner.taskPassed()
    } else {
      val msg = {
        s"failed: Expected exception: ${testAnnotation.expected} " +
        s"took $timeInSeconds sec"
      }
      logFormattedError(packageName, className, methodName, msg)
      runner.taskFailed()
    }
    runner.taskRegisterTotal()
  }

  private[this] def executedWithExceptions(packageName: String,
      className: String, methodName: String, testAnnotation: org.junit.Test,
      timeInSeconds: Double, ex: Throwable) = {
    if (classOf[AssumptionViolatedException[_]].isInstance(ex) ||
        classOf[internal.AssumptionViolatedException].isInstance(ex)) {
      logFormattedInfo(packageName, className, methodName, "started")
      logAssertionWarning(packageName, className, methodName, ex, timeInSeconds)
      runner.taskSkipped()
    } else if (testAnnotation.expected.isInstance(ex)) {
      if (runner.runSettings.verbose)
        logFormattedInfo(packageName, className, methodName, "started")
      runner.taskPassed()
    } else if (testAnnotation.expected == classOf[org.junit.Test.None]) {
      val failedMsg = new StringBuilder
      failedMsg ++= "failed: "
      if (ex.getClass == classOf[AssertionError] &&
          runner.runSettings.logAssert) {
        failedMsg ++= "java.lang." ++= c("AssertionError", ERRMSG) ++= ": "
        failedMsg ++= ex.getMessage
      } else if (runner.runSettings.logExceptionClass) {
        failedMsg ++= ex.getMessage
      } else {
        failedMsg ++= ex.getClass.toString ++= " expected<"
        failedMsg ++= testAnnotation.expected.toString ++= "> but was<"
        failedMsg ++= ex.getClass.toString += '>'
      }
      failedMsg += ','
      val msg = s"$failedMsg took $timeInSeconds sec"
      if (ex.getClass != classOf[AssertionError] ||
          runner.runSettings.logAssert) {
        logFormattedError(packageName, className, methodName, msg, ex)
      } else {
        logFormattedError(packageName, className, methodName, msg)
      }
      runner.taskFailed()
    } else {
      val msg = s"failed: ${ex.getClass}, took $timeInSeconds sec"
      logFormattedError(packageName, className, methodName, msg, ex)
      runner.taskFailed()
    }
    runner.taskRegisterTotal()
  }

  private[this] def logAssertionWarning(packageName: String, className: String,
      methodName: String, ex: Throwable, timeInSeconds: Double): Unit = {
    val msg = "failed: org.junit." +
        c("AssumptionViolatedException", ERRMSG) + ": " + ex.getMessage +
        ", took " + timeInSeconds + " sec"
    logFormattedWarn("Test assumption in test ", packageName, className,
        methodName, msg)
  }

  private[this] def logFormattedInfo(packageName: String, className: String,
      method: String, msg: String): Unit = {
    val fMethod = if (method != null) c(method, NNAME2) else null
    richLogger.info(
        formatLayout("Test ", packageName, c(className, NNAME1), fMethod, msg))
  }

  private[this] def logFormattedWarn(prefix: String, packageName: String,
      className: String, method: String, msg: String): Unit = {
    val fMethod = if (method != null) c(method, ERRMSG) else null
    richLogger.warn(
      formatLayout(prefix, packageName, c(className, NNAME1), fMethod, msg))
  }

  private[this] def logFormattedError(packageName: String, className: String,
      method: String, msg: String, ex: Throwable): Unit = {
    val fMethod = if (method != null) c(method, ERRMSG) else null
    val formattedMsg = formatLayout("Test ", packageName, c(className, NNAME1),
        fMethod, msg)
    richLogger.error(formattedMsg, ex)
  }

  private[this] def logFormattedError(packageName: String, className: String,
      method: String, msg: String): Unit = {
    val fMethod = if (method != null) c(method, ERRMSG) else null
    richLogger.error(
        formatLayout("Test ", packageName, c(className, NNAME1), fMethod, msg))
  }

  private[this] def formatLayout(prefix: String, packageName: String,
      className: String, method: String, msg: String): String = {
    if (method != null) s"$prefix$packageName.$className.$method $msg"
    else s"$prefix$packageName.$className $msg"
  }
}
