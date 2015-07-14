package org.scalajs.junit

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
    continuation(execute(eventHandler, loggers))
  }

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    println
    println(
      s"""
      |JUnitTask.execute(eventHandler = $eventHandler, loggers = $loggers)
      |  taskDef = $taskDef
      |  runner = $runner
    """.stripMargin)

    println(taskDef.fullyQualifiedName)

    val classMetadataTry = {
      Try(TestUtils.loadModule(taskDef.fullyQualifiedName + "$scalajs$junit$hook",
          runner.testClassLoader))
    }

    classMetadataTry match {
      case Success(classMetadata: ScalaJSJUnitTestMetadata) =>

        def executeMethod(method: MethodMetadata, invokeJUnitMethod: String => Unit) = {
          method.getIgnoreAnnotation match {
            case Some(ign) =>
              println(s"Ignoring: ${method.name}")
              if (ign.value != "")
                println(s"| cause: ${ign.value}")
              println(s"+--------")
              println

            case None =>
              println(s"Executing: ${method.name}")
              val testAnnotation = method.getTestAnnotation()
              val (result, time) = {
                val t0 = System.currentTimeMillis
                val result = Try(invokeJUnitMethod(method.id))
                val time = System.currentTimeMillis - t0
                (result, time)
              }

              result match {
                case Success (_) =>
                  testAnnotation match {
                    case Some(test) =>
                      if (test.expected == classOf[org.junit.Test.None]) {
                        println(s"| Success")
                      } else {
                        println(s"| Failed: expected exception ")
                        println(s"| Expected: ${test.expected.getName}")
                      }
                      if (test.timeout != 0 && test.timeout <= time) {
                        println(s"| Timeout: executed in $time ms, expected ${test.timeout} ms")
                      }

                    case None =>
                      println (s"| Success")
                  }

                case Failure (exception) =>
                  testAnnotation match {
                    case Some(test) =>
                      if (test.expected == exception.getClass) {
                        println(s"| Success")
                      } else {
                        println(s"| Failed: expected different exception ")
                        println(s"| Expected: ${test.expected.getName}")
                        println(s"| Received: ${exception.getClass.getName}")
                      }

                    case None =>
                      println (s"| Failed: ${exception.getMessage}")
                      exception.printStackTrace ()
                  }

              }
              println(s"+-------- $time ms")
              println
          }
        }

        val jUnitMetadata = classMetadata.scalajs$junit$metadata

        jUnitMetadata.beforeClassMethod.foreach(executeMethod(_, classMetadata.scalajs$junit$invoke))

        val testClassInstance = classMetadata.scalajs$junit$newInstance
        for (method <- jUnitMetadata.testMethods) {
          jUnitMetadata.beforeMethod.foreach(executeMethod(_, testClassInstance.scalajs$junit$invoke))
          executeMethod(method, testClassInstance.scalajs$junit$invoke)
          jUnitMetadata.afterMethod.foreach(executeMethod(_, testClassInstance.scalajs$junit$invoke))
        }

        jUnitMetadata.afterClassMethod.foreach(executeMethod(_, classMetadata.scalajs$junit$invoke))

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
