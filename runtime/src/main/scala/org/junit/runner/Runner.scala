/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit.runner

import org.junit.runner.notification.RunNotifier

abstract class Runner extends Describable {
  def getDescription(): Description

  def run(notifier: RunNotifier): Unit

  def testCount(): Int = getDescription().testCount()
}
