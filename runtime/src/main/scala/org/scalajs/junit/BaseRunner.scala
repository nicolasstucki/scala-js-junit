package org.scalajs.junit

import sbt.testing._

abstract class BaseRunner(
    val args: Array[String],
    val remoteArgs: Array[String],
    private[junit] val testClassLoader: ClassLoader
) extends Runner {

  protected def newTask(taskDef: TaskDef): Task =
    new JUnitTask(taskDef, this)

  /** Called by task when it has finished executing */
  private[junit] def taskDone(): Unit

  def serializeTask(task: Task, serializer: TaskDef => String): String =
    serializer(task.taskDef)

  def deserializeTask(task: String, deserializer: String => TaskDef): Task =
    newTask(deserializer(task))

}
