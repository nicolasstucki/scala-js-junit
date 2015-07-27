/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit.runner

trait Describable {
  def getDescription(): Description
}
