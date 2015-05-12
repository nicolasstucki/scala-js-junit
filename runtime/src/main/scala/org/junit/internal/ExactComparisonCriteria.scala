package org.junit.internal

import org.junit.Assert

/*
 * Ported from https://github.com/junit-team/junit/blob/master/src/main/java/org/junit/ComparisonFailure.java
 */

class ExactComparisonCriteria extends ComparisonCriteria {
  override protected def assertElementsEqual(expected: AnyRef, actual: AnyRef) {
    Assert.assertEquals(expected, actual)
  }
}