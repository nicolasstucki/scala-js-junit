package org.junit

import org.junit.internal.InexactComparisonCriteria
import org.junit.internal.ExactComparisonCriteria
import org.hamcrest
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert

/*
 * Ported from https://github.com/junit-team/junit/blob/master/src/main/java/org/junit/Assert.java
 */

object Assert {

  /**
   * Asserts that a condition is true. If it isn't it throws an
   * {@link AssertionError} with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param condition condition to be checked
   */
  def assertTrue(message: String, condition: Boolean) {
    if (!condition)
      fail(message)
  }

  /**
   * Asserts that a condition is true. If it isn't it throws an
   * {@link AssertionError} without a message.
   *
   * @param condition condition to be checked
   */
  def assertTrue(condition: Boolean) {
    assertTrue(null, condition)
  }

  /**
   * Asserts that a condition is false. If it isn't it throws an
   * {@link AssertionError} with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param condition condition to be checked
   */
  def assertFalse(message: String, condition: Boolean) {
    assertTrue(message, !condition)
  }

  /**
   * Asserts that a condition is false. If it isn't it throws an
   * {@link AssertionError} without a message.
   *
   * @param condition condition to be checked
   */
  def assertFalse(condition: Boolean) {
    assertFalse(null, condition)
  }

  /**
   * Fails a test with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @see AssertionError
   */
  def fail(message: String) {
    if (message eq null) {
      throw new AssertionError()
    }
    throw new AssertionError(message)
  }

  /**
   * Fails a test with no message.
   */
  def fail() {
    fail(null)
  }

  /**
   * Asserts that two objects are equal. If they are not, an
   * {@link AssertionError} is thrown with the given message. If
   * <code>expected</code> and <code>actual</code> are <code>null</code>,
   * they are considered equal.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expected expected value
   * @param actual actual value
   */
  def assertEquals(message: String, expected: AnyRef, actual: AnyRef) {
    if (!equalsRegardingNull(expected, actual)) {
      (expected, actual) match {
        case (expectedString: String, actualString: String) =>
          val cleanMessage: String = if (message == null) "" else message
          throw new ComparisonFailure(cleanMessage, expectedString, actualString)
        case _ =>
          failNotEquals(message, expected, actual)
      }
    }
  }

  private def equalsRegardingNull(expected: AnyRef, actual: AnyRef): Boolean = {
    if (expected == null)
      actual == null
    else
      isEquals(expected, actual)
  }

  private def isEquals(expected: AnyRef, actual: AnyRef): Boolean = {
    expected == actual
  }

  /**
   * Asserts that two objects are equal. If they are not, an
   * {@link AssertionError} without a message is thrown. If
   * <code>expected</code> and <code>actual</code> are <code>null</code>,
   * they are considered equal.
   *
   * @param expected expected value
   * @param actual the value to check against <code>expected</code>
   */
  def assertEquals(expected: AnyRef, actual: AnyRef) {
    assertEquals(null, expected, actual)
  }

  /**
   * Asserts that two objects are <b>not</b> equals. If they are, an
   * {@link AssertionError} is thrown with the given message. If
   * <code>unexpected</code> and <code>actual</code> are <code>null</code>,
   * they are considered equal.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param unexpected unexpected value to check
   * @param actual the value to check against <code>unexpected</code>
   */
  def assertNotEquals(message: String, unexpected: AnyRef, actual: AnyRef) {
    if (equalsRegardingNull(unexpected, actual))
      failEquals(message, actual)
  }

  /**
   * Asserts that two objects are <b>not</b> equals. If they are, an
   * {@link AssertionError} without a message is thrown. If
   * <code>unexpected</code> and <code>actual</code> are <code>null</code>,
   * they are considered equal.
   *
   * @param unexpected unexpected value to check
   * @param actual the value to check against <code>unexpected</code>
   */
  def assertNotEquals(unexpected: AnyRef, actual: AnyRef) {
    assertNotEquals(null, unexpected, actual)
  }

  private def failEquals(message: String, actual: AnyRef) {
    val checkedMessage = if (message != null) message else "Values should be different"
    fail(s"${checkedMessage}. Actual: $actual")
  }

  /**
   * Asserts that two longs are <b>not</b> equals. If they are, an
   * {@link AssertionError} is thrown with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param unexpected unexpected value to check
   * @param actual the value to check against <code>unexpected</code>
   */
  def assertNotEquals(message: String, unexpected: Long, actual: Long) {
    if (unexpected == actual)
      failEquals(message, java.lang.Long.valueOf(actual))
  }

  /**
   * Asserts that two longs are <b>not</b> equals. If they are, an
   * {@link AssertionError} without a message is thrown.
   *
   * @param unexpected unexpected value to check
   * @param actual the value to check against <code>unexpected</code>
   */
  def assertNotEquals(unexpected: Long, actual: Long) {
    assertNotEquals(null, unexpected, actual)
  }

  /**
   * Asserts that two doubles are <b>not</b> equal to within a positive delta.
   * If they are, an {@link AssertionError} is thrown with the given
   * message. If the unexpected value is infinity then the delta value is
   * ignored. NaNs are considered equal:
   * <code>assertNotEquals(Double.NaN, Double.NaN, *)</code> fails
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param unexpected unexpected value
   * @param actual the value to check against <code>unexpected</code>
   * @param delta the maximum delta between <code>unexpected</code> and
   * <code>actual</code> for which both numbers are still
   * considered equal.
   */
  def assertNotEquals(message: String, unexpected: Double, actual: Double, delta: Double) {
    if (!doubleIsDifferent(unexpected, actual, delta))
      failEquals(message,  java.lang.Double.valueOf(actual))
  }

  /**
   * Asserts that two doubles are <b>not</b> equal to within a positive delta.
   * If they are, an {@link AssertionError} is thrown. If the unexpected
   * value is infinity then the delta value is ignored.NaNs are considered
   * equal: <code>assertNotEquals(Double.NaN, Double.NaN, *)</code> fails
   *
   * @param unexpected unexpected value
   * @param actual the value to check against <code>unexpected</code>
   * @param delta the maximum delta between <code>unexpected</code> and
   * <code>actual</code> for which both numbers are still
   * considered equal.
   */
  def assertNotEquals(unexpected: Double, actual: Double, delta: Double) {
    assertNotEquals(null, unexpected, actual, delta)
  }

  /**
   * Asserts that two floats are <b>not</b> equal to within a positive delta.
   * If they are, an {@link AssertionError} is thrown. If the unexpected
   * value is infinity then the delta value is ignored.NaNs are considered
   * equal: <code>assertNotEquals(Float.NaN, Float.NaN, *)</code> fails
   *
   * @param unexpected unexpected value
   * @param actual the value to check against <code>unexpected</code>
   * @param delta the maximum delta between <code>unexpected</code> and
   * <code>actual</code> for which both numbers are still
   * considered equal.
   */
  def assertNotEquals(unexpected: Float, actual: Float, delta: Float) {
    assertNotEquals(null, unexpected, actual, delta)
  }

  /**
   * Asserts that two object arrays are equal. If they are not, an
   * {@link AssertionError} is thrown with the given message. If
   * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
   * they are considered equal.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expecteds Object array or array of arrays (multi-dimensional array) with
   * expected values.
   * @param actuals Object array or array of arrays (multi-dimensional array) with
   * actual values
   */
  def assertArrayEquals[@specialized(AnyRef, Boolean, Byte, Char, Short, Int, Long) T](
      message: String, expecteds: Array[T], actuals: Array[T]) {
    internalArrayEquals(message, expecteds, actuals)
  }

  /**
   * Asserts that two object arrays are equal. If they are not, an
   * {@link AssertionError} is thrown. If <code>expected</code> and
   * <code>actual</code> are <code>null</code>, they are considered
   * equal.
   *
   * @param expecteds Object array or array of arrays (multi-dimensional array) with
   * expected values
   * @param actuals Object array or array of arrays (multi-dimensional array) with
   * actual values
   */
  def assertArrayEquals[@specialized(AnyRef, Boolean, Byte, Char, Short, Int, Long) T](
      expecteds: Array[T], actuals: Array[T]) {
    assertArrayEquals(null, expecteds, actuals)
  }

//  /**
//   * Asserts that two boolean arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown with the given message. If
//   * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
//   * they are considered equal.
//   *
//   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
//   * okay)
//   * @param expecteds boolean array with expected values.
//   * @param actuals boolean array with expected values.
//   */
//  def assertArrayEquals(message: String, expecteds: Array[Boolean], actuals: Array[Boolean]) {
//    internalArrayEquals(message, expecteds, actuals)
//  }

//  /**
//   * Asserts that two boolean arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown. If <code>expected</code> and
//   * <code>actual</code> are <code>null</code>, they are considered
//   * equal.
//   *
//   * @param expecteds boolean array with expected values.
//   * @param actuals boolean array with expected values.
//   */
//  def assertArrayEquals(expecteds: Array[Boolean], actuals: Array[Boolean]) {
//    assertArrayEquals(null, expecteds, actuals)
//  }

//  /**
//   * Asserts that two byte arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown with the given message.
//   *
//   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
//   * okay)
//   * @param expecteds byte array with expected values.
//   * @param actuals byte array with actual values
//   */
//  def assertArrayEquals(message: String, expecteds: Array[Byte], actuals: Array[Byte]) {
//    internalArrayEquals(message, expecteds, actuals)
//  }

//  /**
//   * Asserts that two byte arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown.
//   *
//   * @param expecteds byte array with expected values.
//   * @param actuals byte array with actual values
//   */
//  def assertArrayEquals(expecteds: Array[Byte], actuals: Array[Byte]) {
//    assertArrayEquals(null, expecteds, actuals)
//  }

//  /**
//   * Asserts that two char arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown with the given message.
//   *
//   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
//   * okay)
//   * @param expecteds char array with expected values.
//   * @param actuals char array with actual values
//   */
//  def assertArrayEquals(message: String, expecteds: Array[Char], actuals: Array[Char]) {
//      internalArrayEquals(message, expecteds, actuals)
//  }

//  /**
//   * Asserts that two char arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown.
//   *
//   * @param expecteds char array with expected values.
//   * @param actuals char array with actual values
//   */
//  def assertArrayEquals(expecteds: Array[Char], actuals: Array[Char]) {
//    assertArrayEquals(null, expecteds, actuals)
//  }

//  /**
//   * Asserts that two short arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown with the given message.
//   *
//   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
//   * okay)
//   * @param expecteds short array with expected values.
//   * @param actuals short array with actual values
//   */
//  def assertArrayEquals(message: String, expecteds: Array[Short], actuals: Array[Short]) {
//    internalArrayEquals(message, expecteds, actuals)
//  }

//    /**
//     * Asserts that two short arrays are equal. If they are not, an
//     * {@link AssertionError} is thrown.
//     *
//     * @param expecteds short array with expected values.
//     * @param actuals short array with actual values
//     */
//    def assertArrayEquals(expecteds: Array[Short], actuals: Array[Short]) {
//        assertArrayEquals(null, expecteds, actuals)
//    }

//    /**
//     * Asserts that two int arrays are equal. If they are not, an
//     * {@link AssertionError} is thrown with the given message.
//     *
//     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
//     * okay)
//     * @param expecteds int array with expected values.
//     * @param actuals int array with actual values
//     */
//    def assertArrayEquals(message: String, expecteds: Array[Int], actuals: Array[Int]) {
//      internalArrayEquals(message, expecteds, actuals)
//    }

//  /**
//   * Asserts that two int arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown.
//   *
//   * @param expecteds int array with expected values.
//   * @param actuals int array with actual values
//   */
//  def assertArrayEquals(expecteds: Array[Int], actuals: Array[Int]) {
//    assertArrayEquals(null, expecteds, actuals)
//  }

//  /**
//   * Asserts that two long arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown with the given message.
//   *
//   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
//   * okay)
//   * @param expecteds long array with expected values.
//   * @param actuals long array with actual values
//   */
//  def assertArrayEquals(message: String, expecteds: Array[Long], actuals: Array[Long])  {
//    internalArrayEquals(message, expecteds, actuals)
//  }

//  /**
//   * Asserts that two long arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown.
//   *
//   * @param expecteds long array with expected values.
//   * @param actuals long array with actual values
//   */
//  def assertArrayEquals(expecteds: Array[Long], actuals: Array[Long]) {
//    assertArrayEquals(null, expecteds, actuals)
//  }

  /**
   * Asserts that two double arrays are equal. If they are not, an
   * {@link AssertionError} is thrown with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expecteds double array with expected values.
   * @param actuals double array with actual values
   * @param delta the maximum delta between <code>expecteds[i]</code> and
   * <code>actuals[i]</code> for which both numbers are still
   * considered equal.
   */
  def assertArrayEquals(message: String, expecteds: Array[Double], actuals: Array[Double], delta: Double)  {
    new InexactComparisonCriteria(delta).arrayEquals(message, expecteds, actuals)
  }

  /**
   * Asserts that two double arrays are equal. If they are not, an
   * {@link AssertionError} is thrown.
   *
   * @param expecteds double array with expected values.
   * @param actuals double array with actual values
   * @param delta the maximum delta between <code>expecteds[i]</code> and
   * <code>actuals[i]</code> for which both numbers are still
   * considered equal.
   */
  def assertArrayEquals(expecteds: Array[Double], actuals: Array[Double], delta: Double) {
    assertArrayEquals(null, expecteds, actuals, delta)
  }

  /**
   * Asserts that two float arrays are equal. If they are not, an
   * {@link AssertionError} is thrown with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expecteds float array with expected values.
   * @param actuals float array with actual values
   * @param delta the maximum delta between <code>expecteds[i]</code> and
   * <code>actuals[i]</code> for which both numbers are still
   * considered equal.
   */
  def assertArrayEquals(message: String, expecteds: Array[Float], actuals: Array[Float], delta: Float)  {
    new InexactComparisonCriteria(delta).arrayEquals(message, expecteds, actuals)
  }

  /**
   * Asserts that two float arrays are equal. If they are not, an
   * {@link AssertionError} is thrown.
   *
   * @param expecteds float array with expected values.
   * @param actuals float array with actual values
   * @param delta the maximum delta between <code>expecteds[i]</code> and
   * <code>actuals[i]</code> for which both numbers are still
   * considered equal.
   */
  def assertArrayEquals(expecteds: Array[Float], actuals: Array[Float], delta: Float) {
    assertArrayEquals(null, expecteds, actuals, delta)
  }

  /**
   * Asserts that two object arrays are equal. If they are not, an
   * {@link AssertionError} is thrown with the given message. If
   * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
   * they are considered equal.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expecteds Object array or array of arrays (multi-dimensional array) with
   * expected values.
   * @param actuals Object array or array of arrays (multi-dimensional array) with
   * actual values
   */
  private def internalArrayEquals(message: String, expecteds: AnyRef, actuals: AnyRef)  {
    new ExactComparisonCriteria().arrayEquals(message, expecteds, actuals)
  }

  /**
   * Asserts that two doubles are equal to within a positive delta.
   * If they are not, an {@link AssertionError} is thrown with the given
   * message. If the expected value is infinity then the delta value is
   * ignored. NaNs are considered equal:
   * <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expected expected value
   * @param actual the value to check against <code>expected</code>
   * @param delta the maximum delta between <code>expected</code> and
   * <code>actual</code> for which both numbers are still
   * considered equal.
   */
  def assertEquals(message: String, expected: Double, actual: Double, delta: Double) {
    if (doubleIsDifferent(expected, actual, delta))
      failNotEquals(message,  java.lang.Double.valueOf(expected),  java.lang.Double.valueOf(actual))
  }

  /**
   * Asserts that two floats are equal to within a positive delta.
   * If they are not, an {@link AssertionError} is thrown with the given
   * message. If the expected value is infinity then the delta value is
   * ignored. NaNs are considered equal:
   * <code>assertEquals(Float.NaN, Float.NaN, *)</code> passes
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expected expected value
   * @param actual the value to check against <code>expected</code>
   * @param delta the maximum delta between <code>expected</code> and
   * <code>actual</code> for which both numbers are still
   * considered equal.
   */
  def assertEquals(message: String, expected: Float, actual: Float, delta: Float) {
    if (floatIsDifferent(expected, actual, delta)) {
      failNotEquals(message, java.lang.Float.valueOf(expected), java.lang.Float.valueOf(actual))
    }
  }

  /**
   * Asserts that two floats are <b>not</b> equal to within a positive delta.
   * If they are, an {@link AssertionError} is thrown with the given
   * message. If the unexpected value is infinity then the delta value is
   * ignored. NaNs are considered equal:
   * <code>assertNotEquals(Float.NaN, Float.NaN, *)</code> fails
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param unexpected unexpected value
   * @param actual the value to check against <code>unexpected</code>
   * @param delta the maximum delta between <code>unexpected</code> and
   * <code>actual</code> for which both numbers are still
   * considered equal.
   */
  def assertNotEquals(message: String, unexpected: Float, actual: Float, delta: Float) {
    if (!floatIsDifferent(unexpected, actual, delta))
      failEquals(message, java.lang.Float.valueOf(actual))
  }

  private def doubleIsDifferent(d1: Double, d2: Double, delta: Double): Boolean = {
    java.lang.Double.compare(d1, d2) != 0 && Math.abs(d1 - d2) > delta
  }

  private def floatIsDifferent(f1: Float, f2: Float, delta: Float): Boolean = {
    java.lang.Float.compare(f1, f2) != 0 && Math.abs(f1 - f2) > delta
  }

  /**
   * Asserts that two longs are equal. If they are not, an
   * {@link AssertionError} is thrown.
   *
   * @param expected expected long value.
   * @param actual actual long value
   */
  def assertEquals(expected: Long, actual: Long) {
    assertEquals(null, expected, actual)
  }

  /**
   * Asserts that two longs are equal. If they are not, an
   * {@link AssertionError} is thrown with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expected long expected value.
   * @param actual long actual value
   */
  def assertEquals(message: String, expected: Long, actual: Long) {
    if (expected != actual)
      failNotEquals(message, java.lang.Long.valueOf(expected), java.lang.Long.valueOf(actual))
  }

//  /**
//   * @deprecated Use
//   *             <code>assertEquals(double expected, double actual, delta: Double)</code>
//   *             instead
//   */
//  @deprecated
//  def assertEquals(expected: Double, actual: Double) {
//    assertEquals(null, expected, actual)
//  }
//
//  /**
//   * @deprecated Use
//   *             <code>assertEquals(message: String, double expected, double actual, delta: Double)</code>
//   *             instead
//   */
//  @deprecated
//  def assertEquals(message: String, expected: Double, actual: Double) {
//    fail("Use assertEquals(expected, actual, delta) to compare floating-point numbers")
//  }

  /**
   * Asserts that two doubles are equal to within a positive delta.
   * If they are not, an {@link AssertionError} is thrown. If the expected
   * value is infinity then the delta value is ignored.NaNs are considered
   * equal: <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
   *
   * @param expected expected value
   * @param actual the value to check against <code>expected</code>
   * @param delta the maximum delta between <code>expected</code> and
   * <code>actual</code> for which both numbers are still
   * considered equal.
   */
  def assertEquals(expected: Double, actual: Double, delta: Double) {
    assertEquals(null, expected, actual, delta)
  }

  /**
   * Asserts that two floats are equal to within a positive delta.
   * If they are not, an {@link AssertionError} is thrown. If the expected
   * value is infinity then the delta value is ignored. NaNs are considered
   * equal: <code>assertEquals(Float.NaN, Float.NaN, *)</code> passes
   *
   * @param expected expected value
   * @param actual the value to check against <code>expected</code>
   * @param delta the maximum delta between <code>expected</code> and
   * <code>actual</code> for which both numbers are still
   * considered equal.
   */

  def assertEquals(expected: Float, actual: Float, delta: Float) {
    assertEquals(null, expected, actual, delta)
  }

  /**
   * Asserts that an object isn't null. If it is an {@link AssertionError} is
   * thrown with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param obj Object to check or <code>null</code>
   */
  def assertNotNull(message: String, obj: AnyRef) {
    assertTrue(message, obj != null)
  }

  /**
   * Asserts that an object isn't null. If it is an {@link AssertionError} is
   * thrown.
   *
   * @param obj Object to check or <code>null</code>
   */
  def assertNotNull(obj: AnyRef) {
    assertNotNull(null, obj)
  }

  /**
   * Asserts that an object is null. If it is not, an {@link AssertionError}
   * is thrown with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param obj Object to check or <code>null</code>
   */
  def assertNull(message: String, obj: AnyRef) {
    if (obj != null)
      failNotNull(message, obj)
  }

  /**
   * Asserts that an object is null. If it isn't an {@link AssertionError} is
   * thrown.
   *
   * @param obj Object to check or <code>null</code>
   */
  def assertNull(obj: AnyRef) {
    assertNull(null, obj)
  }

  private def failNotNull(message: String, actual: AnyRef) {
    val formatted = if (message != null) message + " " else ""
    fail(s"${formatted}expected null, but was:<${actual}>")
  }

  /**
   * Asserts that two objects refer to the same object. If they are not, an
   * {@link AssertionError} is thrown with the given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param expected the expected object
   * @param actual the object to compare to <code>expected</code>
   */
  def assertSame(message: String, expected: AnyRef, actual: AnyRef) {
    if (expected ne actual)
      failNotSame(message, expected, actual)
  }

  /**
   * Asserts that two objects refer to the same object. If they are not the
   * same, an {@link AssertionError} without a message is thrown.
   *
   * @param expected the expected object
   * @param actual the object to compare to <code>expected</code>
   */
  def assertSame(expected: AnyRef, actual: AnyRef) {
    assertSame(null, expected, actual)
  }

  /**
   * Asserts that two objects do not refer to the same object. If they do
   * refer to the same object, an {@link AssertionError} is thrown with the
   * given message.
   *
   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
   * okay)
   * @param unexpected the object you don't expect
   * @param actual the object to compare to <code>unexpected</code>
   */
  def assertNotSame(message: String, unexpected: AnyRef, actual: AnyRef) {
    if (unexpected eq actual)
      failSame(message)
  }

  /**
   * Asserts that two objects do not refer to the same object. If they do
   * refer to the same object, an {@link AssertionError} without a message is
   * thrown.
   *
   * @param unexpected the object you don't expect
   * @param actual the object to compare to <code>unexpected</code>
   */
  def assertNotSame(unexpected: AnyRef, actual: AnyRef) {
    assertNotSame(null, unexpected, actual)
  }

  private def failSame(message: String) {
    if (message == null)
      fail( "expected not same")
    else
      fail(s"${message} expected not same")
  }

  private def failNotSame(message: String, expected: AnyRef, actual: AnyRef) {
    if (message == null)
      fail(s"expected same:<${expected}> was not:<${actual}>")
    else
      fail(s"$message expected same:<${expected}> was not:<${actual}>")
  }

  private def failNotEquals(message: String, expected: AnyRef, actual: AnyRef) {
    fail(format(message, expected, actual))
  }

  private[junit] def format(message: String, expected: AnyRef, actual: AnyRef): String = {
    val formatted = if (message != null && message != "") message + " " else ""
    val expectedString = String.valueOf(expected)
    val actualString = String.valueOf(actual)
    if (expectedString == actualString) {
      val expectedFormatted = formatClassAndValue(expected, expectedString)
      val actualFormatted = formatClassAndValue(actual, actualString)
      s"${formatted}expected: $expectedFormatted but was: $actualFormatted"
    } else {
      s"${formatted}expected:<${expectedString}> but was:<${actualString}>"
    }
  }

  private def formatClassAndValue(value: AnyRef, valueString: String): String = {
    val className = if (value == null) "null" else value.getClass().getName()
    return s"$className<$valueString>"
  }

//  /**
//   * Asserts that two object arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown with the given message. If
//   * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
//   * they are considered equal.
//   *
//   * @param message the identifying message for the {@link AssertionError} (<code>null</code>
//   * okay)
//   * @param expecteds Object array or array of arrays (multi-dimensional array) with
//   * expected values.
//   * @param actuals Object array or array of arrays (multi-dimensional array) with
//   * actual values
//   * @deprecated use assertArrayEquals
//   */
//  @deprecated
//  def assertEquals(message: String, expecteds: Array[AnyRef], actuals: Array[AnyRef]) {
//    assertArrayEquals(message, expecteds, actuals)
//  }
//
//  /**
//   * Asserts that two object arrays are equal. If they are not, an
//   * {@link AssertionError} is thrown. If <code>expected</code> and
//   * <code>actual</code> are <code>null</code>, they are considered
//   * equal.
//   *
//   * @param expecteds Object array or array of arrays (multi-dimensional array) with
//   * expected values
//   * @param actuals Object array or array of arrays (multi-dimensional array) with
//   * actual values
//   * @deprecated use assertArrayEquals
//   */
//  @deprecated
//  def assertEquals(expecteds: Array[AnyRef], actuals: Array[AnyRef]) {
//    assertArrayEquals(expecteds, actuals)
//  }

  /**
   * Asserts that <code>actual</code> satisfies the condition specified by
   * <code>matcher</code>. If not, an {@link AssertionError} is thrown with
   * information about the matcher and failing value. Example:
   *
   * <pre>
   *   assertThat(0, is(1)); // fails:
   *     // failure message:
   *     // expected: is &lt;1&gt;
   *     // got value: &lt;0&gt;
   *   assertThat(0, is(not(1))) // passes
   * </pre>
   *
   * <code>org.hamcrest.Matcher</code> does not currently document the meaning
   * of its type parameter <code>T</code>.  This method assumes that a matcher
   * typed as <code>Matcher&lt;T&gt;</code> can be meaningfully applied only
   * to values that could be assigned to a variable of type <code>T</code>.
   *
   * @param <T> the static type accepted by the matcher (this can flag obvious
   * compile-time problems such as {@code assertThat(1, is("a"))}
   * @param actual the computed value being compared
   * @param matcher an expression, built of {@link Matcher}s, specifying allowed
   * values
   * @see org.hamcrest.CoreMatchers
   * @see org.hamcrest.MatcherAssert
   */
  def assertThat[T](actual: T, matcher: Matcher[T]) {
    assertThat("", actual, matcher)
  }

  /**
   * Asserts that <code>actual</code> satisfies the condition specified by
   * <code>matcher</code>. If not, an {@link AssertionError} is thrown with
   * the reason and information about the matcher and failing value. Example:
   *
   * <pre>
   *   assertThat(&quot;Help! Integers don't work&quot;, 0, is(1)); // fails:
   *     // failure message:
   *     // Help! Integers don't work
   *     // expected: is &lt;1&gt;
   *     // got value: &lt;0&gt;
   *   assertThat(&quot;Zero is one&quot;, 0, is(not(1))) // passes
   * </pre>
   *
   * <code>org.hamcrest.Matcher</code> does not currently document the meaning
   * of its type parameter <code>T</code>.  This method assumes that a matcher
   * typed as <code>Matcher&lt;T&gt;</code> can be meaningfully applied only
   * to values that could be assigned to a variable of type <code>T</code>.
   *
   * @param reason additional information about the error
   * @param <T> the static type accepted by the matcher (this can flag obvious
   * compile-time problems such as {@code assertThat(1, is("a"))}
   * @param actual the computed value being compared
   * @param matcher an expression, built of {@link Matcher}s, specifying allowed
   * values
   * @see org.hamcrest.CoreMatchers
   * @see org.hamcrest.MatcherAssert
   */
  def assertThat[T](reason: String, actual: T, matcher: Matcher[T]) {
    MatcherAssert.assertThat(reason, actual, matcher);
  }

}