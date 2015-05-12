package org.junit.internal

import org.junit.Assert


/*
 * Ported from https://github.com/junit-team/junit/blob/master/src/main/java/org/junit/ComparisonFailure.java
 */

class InexactComparisonCriteria private (
    val fDelta: AnyRef
  ) extends ComparisonCriteria {

  def this(delta: Double) = this(delta.asInstanceOf[AnyRef])

  def this(delta: Float) = this(delta.asInstanceOf[AnyRef])

  override protected def assertElementsEqual(expected: AnyRef, actual: AnyRef) {
    if (expected.isInstanceOf[Double]) {
        Assert.assertEquals(
            expected.asInstanceOf[Double],
            actual.asInstanceOf[Double],
            fDelta.asInstanceOf[Double])
    } else {
        Assert.assertEquals(
            expected.asInstanceOf[Float],
            actual.asInstanceOf[Float],
            fDelta.asInstanceOf[Float]);
    }
  }
}