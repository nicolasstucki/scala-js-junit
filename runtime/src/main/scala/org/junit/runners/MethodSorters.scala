/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit.runners

object MethodSorters {

  private lazy val _NAME_ASCENDING = new MethodSorters((x, y) => x.compareTo(y))
  private lazy val _JVM = new MethodSorters((x, y) => 0)
  private lazy val _DEFAULT = new MethodSorters((x, y) => 0)

  def NAME_ASCENDING = _NAME_ASCENDING

  def JVM = _JVM

  def DEFAULT = _DEFAULT
}

class MethodSorters private[runners](f: (String, String) => Int) {
  def comparator: Ordering[String] = {
    new Ordering[String] {
      def compare (x: String, y: String): Int = f(x, y)
    }
  }
}
