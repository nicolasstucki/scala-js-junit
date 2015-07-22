package org.scalajs.junit

import com.novocode.junit.Ansi._
import com.novocode.junit.RunSettings
import sbt.testing._

final class SlaveRunner(
    args: Array[String],
    remoteArgs: Array[String],
    testClassLoader: ClassLoader,
    send: String => Unit,
    runSettings: RunSettings
) extends BaseRunner(args, remoteArgs, testClassLoader, runSettings) {

  /** Number of tasks completed on this node */
  private[this] var doneCount = 0
  private[this] var passedCount = 0
  private[this] var failedCount = 0
  private[this] var skippedCount = 0

  /** Whether we have seen a Hello message from the master yet */
  private[this] var seenHello = false

  // Notify master of our existence
  send("s")

  def tasks(taskDefs: Array[TaskDef]): Array[Task] = {
    ensureSeenHello()

    // Notify master of new tasks
    send("t" + taskDefs.length)
    taskDefs.map(newTask)
  }

  def done(): String = {
    ensureSeenHello()
    send("d" + SlaveDone(doneCount, passedCount, failedCount, skippedCount).serialize)
    val passed = 0
    val failed = 0
    val ignored = 0
    val total = passed + failed + ignored
    val time = 0
    c(s"Test run finished: $failed failed, $ignored ignored, $total total, ${time.toDouble / 1000}s", INFO)
  }

  private[junit] def taskDone(): Unit = doneCount += 1
  private[junit] def taskPassed(): Unit = passedCount += 1
  private[junit] def taskFailed(): Unit = failedCount += 1
  private[junit] def taskSkipped(): Unit = skippedCount += 1

  private[junit] def taskPassedCount(): Int = passedCount
  private[junit] def taskFailedCount(): Int = failedCount
  private[junit] def taskSkippedCount(): Int = skippedCount

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

object SlaveDone {
  def deserialize(str: String): SlaveDone = {
    val split = str.split(':')
    if (split.length != 4)
      throw new Exception(str)
    new SlaveDone(split(0).toInt, split(1).toInt, split(2).toInt, split(3).toInt)
  }
}

case class SlaveDone(done: Int, passed: Int, failed: Int, skipped: Int) {
  def serialize(): String = Seq(done, passed, failed, skipped).mkString(":")
}