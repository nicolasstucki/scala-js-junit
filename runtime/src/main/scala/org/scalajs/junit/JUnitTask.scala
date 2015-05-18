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
      case Success(testInstance: ScalaJSJUnitTest) =>

        println(s"testInstance: testInstance")
        println(s"listTestMethods: ${testInstance.getJUnitDefinitions()}")

        def executeMethods(methods: List[ScalaJSJUnitTest.Method]) = {
          for (method <- methods) {
            println(s"Executing: ${method.name}")
            method.invokeTry() match {
              case Success(_) =>
                println(s"| Success")
              case Failure(exception) =>
                println(s"| Failed: ${exception.getMessage}")
                exception.printStackTrace()
            }
          }
        }

        val jUnitDeffinition = testInstance.getJUnitDefinitions()
        executeMethods(jUnitDeffinition.beforeClassMethods)
        executeMethods(jUnitDeffinition.beforeMethods)
        executeMethods(jUnitDeffinition.testMethods)
        executeMethods(jUnitDeffinition.afterMethods)
        executeMethods(jUnitDeffinition.afterClassMethods)

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
