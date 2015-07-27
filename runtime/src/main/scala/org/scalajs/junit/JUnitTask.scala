package org.scalajs.junit

import com.novocode.junit.{Ansi, RichLogger}
import Ansi._
import sbt.testing._
import org.scalajs.testinterface.TestUtils
import scala.util.{Try, Success, Failure}

final class JUnitTask(val taskDef: TaskDef, runner: JUnitBaseRunner)
    extends sbt.testing.Task {

  def tags: Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger],
        continuation: Array[Task] => Unit): Unit = {

    val richLogger = new RichLogger(loggers, runner.runSettings,
        taskDef.fullyQualifiedName)

    if (runner.runSettings.verbose)
      richLogger.info(c("Test run started", INFO))

    val startTime = System.currentTimeMillis
    val tasks = execute(eventHandler, loggers)

    if (runner.runSettings.verbose) {
      val time = System.currentTimeMillis - startTime
      val failed = runner.taskFailedCount()
      val ignored = runner.taskIgnoredCount()
      val total = runner.taskTotalCount()
      val msg = Seq(
        c("Test run finished:", INFO),
        c(s"$failed failed,", if (failed == 0) INFO else ERRCOUNT),
        c(s"$ignored ignored,", if (ignored == 0) INFO else IGNCOUNT),
        c(s"$total total,", INFO),
        c(s"${time.toDouble / 1000}s", INFO))
      richLogger.info(msg.mkString(" "))
    }
    continuation(tasks)
  }

  def execute(eventHandler: EventHandler,
      loggers: Array[Logger]): Array[Task] = {

    val richLogger = new RichLogger(loggers, runner.runSettings,
        taskDef.fullyQualifiedName)

    val hookName = taskDef.fullyQualifiedName + "$scalajs$junit$hook"

    Try(TestUtils.loadModule(hookName, runner.testClassLoader)) match {
      case Success(classMetadata: JUnitTestMetadata) =>
        new JUnitExecuteTest(taskDef.fullyQualifiedName, runner, classMetadata,
          richLogger).executeTests()

      case Success(_) =>
        richLogger.error("Error while loading test class: " +
            taskDef.fullyQualifiedName + ", expected " + hookName +
            " to extend JUnitTestMetadata")

      case Failure(exception) =>
        richLogger.error("Error while loading test class: " +
            taskDef.fullyQualifiedName, exception)
    }

    runner.taskDone()
    Array()
  }

  private class DummyEvent(taskDef: TaskDef,
      t: Option[Throwable]) extends Event {
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
