package org.scalajs.junit

import com.novocode.junit.RunSettings
import sbt.testing._

final class JUnitSlaveRunner(
    args: Array[String],
    remoteArgs: Array[String],
    testClassLoader: ClassLoader,
    send: String => Unit,
    runSettings: RunSettings)
    extends JUnitBaseRunner(args, remoteArgs, testClassLoader, runSettings) {

  private[this] var doneCount = 0
  private[this] var passedCount = 0
  private[this] var failedCount = 0
  private[this] var ignoredCount = 0
  private[this] var skippedCount = 0
  private[this] var totalCount = 0

  private[this] var seenHello = false

  send("s")

  def tasks(taskDefs: Array[TaskDef]): Array[Task] = {
    ensureSeenHello()
    send("t" + taskDefs.length)
    taskDefs.map(newTask)
  }

  def done(): String = {
    ensureSeenHello()
    send("d" + JUnitBaseRunner.Done(doneCount, passedCount, failedCount,
        ignoredCount, skippedCount, totalCount).serialize)
    ""
  }

  private[junit] def taskDone(): Unit = doneCount += 1
  private[junit] def taskPassed(): Unit = passedCount += 1
  private[junit] def taskFailed(): Unit = failedCount += 1
  private[junit] def taskIgnored(): Unit = ignoredCount += 1
  private[junit] def taskSkipped(): Unit = skippedCount += 1
  private[junit] def taskRegisterTotal(): Unit = totalCount += 1

  private[junit] def taskPassedCount(): Int = passedCount
  private[junit] def taskFailedCount(): Int = failedCount
  private[junit] def taskIgnoredCount(): Int = ignoredCount
  private[junit] def taskSkippedCount(): Int = skippedCount
  private[junit] def taskTotalCount(): Int = totalCount

  def receiveMessage(msg: String): Option[String] = {
    assert(msg == "Hello")
    seenHello = true
    None // <- ignored
  }

  override def serializeTask(task: Task,
      serializer: TaskDef => String): String = {
    ensureSeenHello()
    super.serializeTask(task, serializer)
  }

  override def deserializeTask(task: String,
      deserializer: String => TaskDef): Task = {
    ensureSeenHello()
    super.deserializeTask(task, deserializer)
  }

  private def ensureSeenHello(): Unit = {
    if (!seenHello)
      throw new IllegalStateException("Have not seen the master yet")
  }
}
