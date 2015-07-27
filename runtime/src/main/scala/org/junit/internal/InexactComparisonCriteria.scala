/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit.internal

import org.junit.Assert

class InexactComparisonCriteria private(val fDelta: AnyRef)
    extends ComparisonCriteria {

  def this(delta: Double) =
    this(delta.asInstanceOf[AnyRef])

  def this(delta: Float) =
    this(delta.asInstanceOf[AnyRef])

  override protected def assertElementsEqual(expected: AnyRef,
      actual: AnyRef): Unit = {
    expected match {
      case expected: java.lang.Double =>
        Assert.assertEquals(expected, actual.asInstanceOf[Double],
            fDelta.asInstanceOf[Double])

      case _ =>
        Assert.assertEquals(expected.asInstanceOf[Float],
            actual.asInstanceOf[Float], fDelta.asInstanceOf[Float])
    }
  }
}
