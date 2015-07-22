package org.scalajs.junit

import com.novocode.junit.RunSettings
import sbt.testing._
import java.util.concurrent.atomic.AtomicInteger

final class MasterRunner(
    args: Array[String],
    remoteArgs: Array[String],
    testClassLoader: ClassLoader,
    runSettings: RunSettings
) extends BaseRunner(args, remoteArgs, testClassLoader, runSettings) {

  /** Number of tasks registered in the whole system */
  private[this] val registeredCount = new AtomicInteger(0)

  /** Number of tasks completed in the whole system */
  private[this] val doneCount = new AtomicInteger(0)

  private[this] val passedCount = new AtomicInteger(0)
  private[this] val failedCount = new AtomicInteger(0)
  private[this] val skippedCount = new AtomicInteger(0)

  /** Number of running slaves in the whole system */
  private[this] val slaveCount = new AtomicInteger(0)

  def tasks(taskDefs: Array[TaskDef]): Array[Task] = {
    registeredCount.addAndGet(taskDefs.length)
    taskDefs.map(newTask)
  }

  def done(): String = {
    val slaves = slaveCount.get
    val registered = registeredCount.get
    val done = doneCount.get

    if (slaves > 0)
      throw new IllegalStateException(s"There are still $slaves slaves running")

    if (registered != done) {
      throw new IllegalStateException(
        s"$registered task(s) were registered, $done were executed")
    } else {
      // TODO
      val passed = passedCount.get
      val failed = failedCount.get
      val skipped = skippedCount.get
      val total = passed + failed + skipped
      s"""Passed: Total $total,
         |Errors $failed,
         |Passed $passed,
         |${if (skipped != 0) s"Skipped $skipped" else ""}
         |""".stripMargin.replace('\n', ' ')
    }
  }

  private[junit] def taskDone(): Unit = doneCount.incrementAndGet()
  private[junit] def taskPassed(): Unit = passedCount.incrementAndGet()
  private[junit] def taskFailed(): Unit = failedCount.incrementAndGet()
  private[junit] def taskSkipped(): Unit = skippedCount.incrementAndGet()

  private[junit] def taskPassedCount(): Int = passedCount.get
  private[junit] def taskFailedCount(): Int = failedCount.get
  private[junit] def taskSkippedCount(): Int = skippedCount.get

  def receiveMessage(msg: String): Option[String] = msg(0) match {
    case 's' =>
      slaveCount.incrementAndGet()
      // Send Hello message back
      Some("Hello")
    case 't' =>
      // Slave notifies us of registration of tasks
      registeredCount.addAndGet(msg.tail.toInt)
      None
    case 'd' =>
      // Slave notifies us of completion of tasks
      val slaveDone = SlaveDone.deserialize(msg.tail)
      doneCount.addAndGet(slaveDone.done)
      passedCount.addAndGet(slaveDone.passed)
      failedCount.addAndGet(slaveDone.failed)
      skippedCount.addAndGet(slaveDone.skipped)
      slaveCount.decrementAndGet()
      None
  }

}
