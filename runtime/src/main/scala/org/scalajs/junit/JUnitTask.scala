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

    println(taskDef.fullyQualifiedName)
    val tryInstance = Try(TestUtils.newInstance(taskDef.fullyQualifiedName, runner.testClassLoader)(Seq()))
    // Fixme: Not loading module
    val moduleOption: Option[ScalaJSJUnitTest] = Some(TestUtils.loadModule(taskDef.fullyQualifiedName, runner.testClassLoader)).collect {
      case module: ScalaJSJUnitTest => module
    }

    tryInstance match {
      case Success(testInstance: ScalaJSJUnitTest) =>

        println(s"testInstance: ${testInstance.getJUnitMetadata$}")

        def executeMethods(methods: List[AnnotatedMethod], invokeJUnitMethod: String => Unit) = {
          for (method <- methods) {
            method.getIgnoreAnnotation match {
              case Some(ign) =>
                println(s"Ignoring: ${method.name}")
                if (ign.value != "")
                  println(s"   cause: ${ign.value}")

              case None =>
                println(s"Executing: ${method.name}")
                Try (invokeJUnitMethod(method.id)) match {
                  case Success (_) =>
                    println (s"| Success")
                  case Failure (exception) =>
                    println (s"| Failed: ${exception.getMessage}")
                    exception.printStackTrace ()
                }
            }
          }
        }

        val jUnitMetadata = testInstance.getJUnitMetadata$()
        val jUnitModuleMetadataOption = moduleOption.map(_.getJUnitMetadata$())

        jUnitModuleMetadataOption match {
          case Some(jUnitModuleMetadata) =>
            executeMethods(jUnitModuleMetadata.beforeClassMethods, moduleOption.get.invokeJUnitMethod$)
          case None =>
        }
        executeMethods(jUnitMetadata.beforeMethods, testInstance.invokeJUnitMethod$)
        executeMethods(jUnitMetadata.testMethods, testInstance.invokeJUnitMethod$)
        executeMethods(jUnitMetadata.afterMethods, testInstance.invokeJUnitMethod$)
        jUnitModuleMetadataOption match {
          case Some(jUnitModuleMetadata) =>
            executeMethods(jUnitModuleMetadata.afterClassMethods, moduleOption.get.invokeJUnitMethod$)
          case None =>
        }

      case Success(_) =>
        // TODO warn, class should be a subclass of org.scalajs.junit.Test
        // if framework works correctly, this case should never happend
      case Failure(exception) =>
        // TODO
        println(exception)
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
