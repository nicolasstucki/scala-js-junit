package org.junit.internal;

import java.lang.reflect.{Array => ReflectArray}

import org.junit.Assert

/*
 * Ported from https://github.com/junit-team/junit/blob/master/src/main/java/org/junit/ComparisonFailure.java
 */

/**
 * Defines criteria for finding two items "equal enough". Concrete subclasses
 * may demand exact equality, or, for example, equality within a given delta.
 */
abstract class ComparisonCriteria {
  /**
   * Asserts that two arrays are equal, according to the criteria defined by
   * the concrete subclass. If they are not, an {@link AssertionError} is
   * thrown with the given message. If <code>expecteds</code> and
   * <code>actuals</code> are <code>null</code>, they are considered equal.
   *
   * @param message the identifying message for the {@link AssertionError} (
   * <code>null</code> okay)
   * @param expecteds Object array or array of arrays (multi-dimensional array) with
   * expected values.
   * @param actuals Object array or array of arrays (multi-dimensional array) with
   * actual values
   */
  def arrayEquals(message: String, expecteds: AnyRef, actuals: AnyRef) {
    arrayEquals(message, expecteds, actuals, true)
  }

  // Partial port of java.util.Arrays
  private object Arrays {

    /** Returns true if the two specified arrays are deeply equal to one another. Unlike the equals(Object[],Object[]) method, this method is appropriate for use with nested arrays of arbitrary depth.
     * Two array references are considered deeply equal if both are null, or if they refer to arrays that contain the same number of elements and all corresponding pairs of elements in the two arrays are deeply equal.
     * Two possibly null elements e1 and e2 are deeply equal if any of the following conditions hold:
     *
     * e1 and e2 are both arrays of object reference types, and Arrays.deepEquals(e1, e2) would return true
     * e1 and e2 are arrays of the same primitive type, and the appropriate overloading of Arrays.equals(e1, e2) would return true.
     * e1 == e2
     * e1.equals(e2) would return true.
     *
     * Note that this definition permits null elements at any depth.
     * If either of the specified arrays contain themselves as elements either directly or indirectly through one or more levels of arrays, the behavior of this method is undefined.
     *
     * Parameters:
     *   a1 - one array to be tested for equality
     *   a2 - the other array to be tested for equality
     * Returns: true if the two arrays are equal
     */
    def deepEquals(a1: Array[AnyRef], a2: Array[AnyRef]): Boolean = {
      if ((a1 eq a2) || a1 == a2)
        true
      else if (a1 == null || a2 == null || a1.length != a1.length)
        false
      else {
        (a1 zip a2) forall {
          case (e1, e2) if (e1 eq e2) || e1 == e2 => true
          case (e1: Array[AnyRef], e2: Array[AnyRef]) => deepEquals(e1, e2)
          case _ => false
        }
      }
    }
  }

  private def arrayEquals(message: String, expecteds: AnyRef, actuals: AnyRef, outer: Boolean) {
    if (expecteds == actuals || Arrays.deepEquals(Array(expecteds), Array(actuals))) {
      // The reflection-based loop below is potentially very slow, especially for primitive
      // arrays. The deepEquals check allows us to circumvent it in the usual case where
      // the arrays are exactly equal.
      return
    }

    val header = if (message == null) "" else s"$message: "

    // Only include the user-provided message in the outer exception.
    val exceptionMessage = if (outer) header else ""
    val expectedsLength = assertArraysAreSameLength(expecteds, actuals, exceptionMessage)

    for (i <- 0 until expectedsLength) {
      val expected = ReflectArray.get(expecteds, i)
      val actual = ReflectArray.get(actuals, i)

      if (isArray(expected) && isArray(actual)) {
        try {
          arrayEquals(message, expected, actual, false)
        } catch {
          case e: ArrayComparisonFailure =>
            e.addDimension(i)
            throw e
          case e: AssertionError =>
            // Array lengths differed.
            throw new ArrayComparisonFailure(header, e, i)
        }
      } else {
        try {
            assertElementsEqual(expected, actual)
        } catch {
          case e: AssertionError =>
            throw new ArrayComparisonFailure(header, e, i)
        }
      }
    }
  }

  private def isArray(expected: AnyRef): Boolean = {
    expected != null && expected.getClass().isArray()
  }

  private def assertArraysAreSameLength(expecteds: AnyRef, actuals: AnyRef,
        header: String): Int = {
    if (expecteds == null) {
      Assert.fail(header + "expected array was null")
    }
    if (actuals == null) {
      Assert.fail(header + "actual array was null")
    }
    val actualsLength = ReflectArray.getLength(actuals)
    val expectedsLength = ReflectArray.getLength(expecteds)
    if (actualsLength != expectedsLength) {
      Assert.fail(
          s"${header}array lengths differed, expected.length=$expectedsLength actual.length=$actualsLength")
    }
    expectedsLength
  }

  protected def assertElementsEqual(expected: AnyRef, actual: AnyRef): Unit

}