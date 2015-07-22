package org.scalajs.junit

import java.util

import com.novocode.junit.{Ansi, RichLogger, RunSettings}
import Ansi._
import sbt.testing._
import org.scalajs.testinterface.TestUtils
import scala.util.{Try, Success, Failure}

final class JUnitTask(
  val taskDef: TaskDef,
  runner: BaseRunner
) extends sbt.testing.Task {


  def tags: Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger],
        continuation: Array[Task] => Unit): Unit = {

    val richLogger = new RichLogger(loggers, runner.runSettings, taskDef.fullyQualifiedName)
    if (runner.runSettings.verbose)
      richLogger.info(c("Test run started", INFO))

    val startTime = System.currentTimeMillis
    val tasks = execute(eventHandler, loggers)

    if (runner.runSettings.verbose) {
      val time = System.currentTimeMillis - startTime
      val passed = runner.taskPassedCount()
      val failed = runner.taskFailedCount()
      val skipped = runner.taskSkippedCount()
      val total = passed + failed + skipped
      val msg = Seq(
        c(" Test run finished:", INFO),
        c(s"$failed failed,", if (failed == 0) INFO else ERRCOUNT),
        c(s"$skipped ignored,", if (skipped == 0) INFO else IGNCOUNT),
        c(s"$total total,", INFO),
        c(s"${time}s", INFO))
      richLogger.info(msg.mkString(" "))
    }
    continuation(tasks)
  }

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    val richLogger = new RichLogger(loggers, runner.runSettings, taskDef.fullyQualifiedName)

    val packageName = taskDef.fullyQualifiedName.split('.').init.mkString(".")
    val className = taskDef.fullyQualifiedName.split('.').last

    val classMetadataTry = {
      Try(TestUtils.loadModule(taskDef.fullyQualifiedName + "$scalajs$junit$hook",
          runner.testClassLoader))
    }

    classMetadataTry match {
      case Success(classMetadata: ScalaJSJUnitTestMetadata) =>

        val jUnitMetadata = classMetadata.scalajs$junit$metadata
        val testClassInstance = classMetadata.scalajs$junit$newInstance

        for (method <- jUnitMetadata.testMethods) {
          method.getIgnoreAnnotation match {
            case Some(ign) =>
              richLogger.info(formatInfo(packageName, className, method.name, "ignored"))
              runner.taskSkipped()

            case None =>
              val testAnnotation = method.getTestAnnotation().get
              val (result, time) = {
                val t0 = System.currentTimeMillis
                val result = Try(testClassInstance.scalajs$junit$invoke(method.id))
                val time = System.currentTimeMillis - t0
                (result, time)
              }
              val timeInSeconds = time.toDouble / 1000

              result match {
                case Success(_) =>
                  if (testAnnotation.expected == classOf[org.junit.Test.None]) {
                    if (runner.runSettings.verbose)
                      richLogger.info(formatInfo(packageName, className, method.name, "started"))
                    runner.taskPassed()
                  } else {
                    val msg = {
                      s"failed: Expected exception: ${testAnnotation.expected} " +
                      s"took $timeInSeconds sec"
                    }
                    richLogger.error(formatError(packageName, className, method.name, msg))
                    runner.taskFailed()
                  }


                case Failure(exception) =>
                  if (testAnnotation.expected == exception.getClass) {
                    if (runner.runSettings.verbose)
                      richLogger.info(formatInfo(packageName, className, method.name, "started"))
                    runner.taskPassed()
                  } else if (testAnnotation.expected == classOf[org.junit.Test.None]) {
                    val failedMsg = "failed: " + {
                      if (runner.runSettings.logExceptionClass) {
                        exception.getMessage
                      } else {
                        exception.getClass + " expected<" +
                        testAnnotation.expected + "> but was<" +
                        exception.getClass + ">"
                      }
                    } + ","

                    val msg = failedMsg + s" took $timeInSeconds sec"
                    richLogger.error(formatError(packageName, className, method.name, msg), exception)
                    runner.taskFailed()
                  } else {
                    val msg = s"failed: ${exception.getClass}, took $timeInSeconds sec"
                    richLogger.error(formatError(packageName, className, method.name, msg), exception)
                    runner.taskFailed()
                  }
              }
              if (testAnnotation.timeout != 0 && testAnnotation.timeout <= time)
                richLogger.warn(s"| Timeout: took $timeInSeconds sec, expected ${testAnnotation.timeout.toDouble / 1000} sec")
          }
        }

      case Success(_) =>
        // TODO warn, class should be a subclass of org.scalajs.junit.Test
        // if framework works correctly, this case should never happend
        runner.taskFailed()
      case Failure(exception) =>
        // TODO
//        println(exception)
        runner.taskFailed()
    }

    runner.taskDone()
    Array()
  }

  private def formatInfo(packageName: String, className: String, method: String, msg: String): String =
    formatLayout(packageName, c(className, NNAME1), c(method, NNAME2), msg)

  private def formatError(packageName: String, className: String, method: String, msg: String): String =
    formatLayout(packageName, c(className, NNAME1), c(method, ERRMSG), msg)

  private def formatLayout(packageName: String, className: String, method: String, msg: String): String =
    s"Test $packageName.$className.$method $msg"

  private class DummyEvent(taskDef: TaskDef, t: Option[Throwable]) extends Event {
    val fullyQualifiedName: String = taskDef.fullyQualifiedName
    val fingerprint: Fingerprint = taskDef.fingerprint
    val selector: Selector = new SuiteSelector

    val status: Status =
      if (t.isDefined) Status.Error else Status.Success

    val throwable: OptionalThrowable =
      t.fold(new OptionalThrowable)(new OptionalThrowable(_))

    val duration: Long = -1L
  }

}
