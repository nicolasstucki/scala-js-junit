package org.junit.runners

import org.junit.runner.Description
import org.junit.runner.manipulation.{Filter, Sorter}
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.FrameworkMethod

class BlockJUnit4ClassRunner(testClass: Class[_]) extends ParentRunner[FrameworkMethod](testClass) {
  // Dummy for classOf[...]

  def getDescription(): Description = Description.EMPTY

  def run(notifier: RunNotifier): Unit = { }

  def filter(filter: Filter): Unit = { }

  def sort(sorter: Sorter): Unit = { }
}

final class JUnit4(klass: Class[_]) extends BlockJUnit4ClassRunner(klass)
