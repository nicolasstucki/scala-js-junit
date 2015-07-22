package org.scalajs.junit

import com.novocode.junit.RunSettings
import sbt.testing._

abstract class BaseRunner(
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
  private[junit] def taskSkipped(): Unit

  private[junit] def taskPassedCount(): Int
  private[junit] def taskFailedCount(): Int
  private[junit] def taskSkippedCount(): Int

  def serializeTask(task: Task, serializer: TaskDef => String): String =
    serializer(task.taskDef)

  def deserializeTask(task: String, deserializer: String => TaskDef): Task =
    newTask(deserializer(task))

}
