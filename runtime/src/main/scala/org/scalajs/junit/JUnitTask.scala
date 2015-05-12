package org.scalajs.junit

import sbt.testing._
import org.scalajs.testinterface.TestUtils
import scala.util.{Try, Success, Failure}
import scala.scalajs.js.Dynamic
import scala.util.Failure


final class JUnitTask(
  val taskDef: TaskDef,
  runner: BaseRunner
) extends sbt.testing.Task {

  def tags: Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger],
              continuation: Array[Task] => Unit): Unit = {
    continuation(execute(eventHandler, loggers))
  }

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    println(s"""
      |JUnitTask.execute(eventHandler = $eventHandler, loggers = $loggers)
      |  taskDef = $taskDef
      |  runner = $runner
    """.stripMargin)

    val tryInstance = Try(TestUtils.newInstance(taskDef.fullyQualifiedName, runner.testClassLoader)(Seq()))

    tryInstance match {
      case Success(testInstance: Test) =>

        println(s"testInstance: testInstance")
        println(s"listTestMethods: ${testInstance.listTestMethods()}")

        for(testMethod <- testInstance.listTestMethods()) {
          println(s"Executing: ${testMethod.name}")

          testMethod.executor() match {
            case Success(_) =>
              println(s"Executed: ${testMethod.name}")
            case Failure(exception) =>
              println(s"Failed: ${testMethod.name}")
              exception.printStackTrace()
          }

        }
      case Success(_) =>
        // TODO warn, class should be a subclass of org.scalajs.junit.Test
        // if framework works correctly, this case should never happen
      case Failure(exception) =>
        // TODO
    }

    runner.taskDone()
    Array()
  }

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
