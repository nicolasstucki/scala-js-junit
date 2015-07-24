package org.scalajs.junit

import com.novocode.junit.RunSettings
import sbt.testing._

abstract class JUnitBaseRunner(
    val args: Array[String],
    val remoteArgs: Array[String],
    private[junit] val testClassLoader: ClassLoader,
    private[junit] val runSettings: RunSettings
) extends Runner {

  protected def newTask(taskDef: TaskDef): Task =
    new JUnitTask(taskDef, this)

  /** Called by task when it has finished executing */
  private[junit] def taskDone(): Unit
  private[junit] def taskPassed(): Unit
  private[junit] def taskFailed(): Unit
  private[junit] def taskIgnored(): Unit
  private[junit] def taskSkipped(): Unit
  private[junit] def taskRegisterTotal(): Unit

  private[junit] def taskPassedCount(): Int
  private[junit] def taskFailedCount(): Int
  private[junit] def taskIgnoredCount(): Int
  private[junit] def taskSkippedCount(): Int
  private[junit] def taskTotalCount(): Int

  def serializeTask(task: Task, serializer: TaskDef => String): String =
    serializer(task.taskDef)

  def deserializeTask(task: String, deserializer: String => TaskDef): Task =
    newTask(deserializer(task))

}


object JUnitBaseRunner {
  object Done {
    def deserialize(str: String): Done = {
      val split = str.split(':')
      if (split.length != 6) {
        throw new Exception(str)
      } else {
        new Done(split(0).toInt, split(1).toInt, split(2).toInt, split(3).toInt,
            split(4).toInt, split(5).toInt)
      }
    }
  }

  case class Done(done: Int, passed: Int, failed: Int, ignored: Int, skipped: Int, total: Int) {
    def serialize(): String = Seq(done, passed, failed, ignored, skipped, total).mkString(":")
  }
}