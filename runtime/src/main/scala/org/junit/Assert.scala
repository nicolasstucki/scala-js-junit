/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit

import org.junit.internal.InexactComparisonCriteria
import org.junit.internal.ExactComparisonCriteria
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert

object Assert {
  def assertTrue(message: String, condition: Boolean): Unit =
    if (!condition) fail(message)

  def assertTrue(condition: Boolean): Unit =
    assertTrue(null, condition)

  def assertFalse(message: String, condition: Boolean): Unit =
    assertTrue(message, !condition)

  def assertFalse(condition: Boolean): Unit =
    assertFalse(null, condition)

  def fail(message: String): Unit = {
    if (message eq null)
      throw new AssertionError()
    throw new AssertionError(message)
  }

  def fail(): Unit =
    fail(null)

  def assertEquals(message: String, expected: AnyRef, actual: AnyRef): Unit = {
    if (!equalsRegardingNull(expected, actual)) {
      (expected, actual) match {
        case (expectedString: String, actualString: String) =>
          val cleanMsg: String = if (message == null) "" else message
          throw new ComparisonFailure(cleanMsg, expectedString, actualString)

        case _ =>
          failNotEquals(message, expected, actual)
      }
    }
  }

  private def equalsRegardingNull(expected: AnyRef, actual: AnyRef): Boolean =
    if (expected == null) actual == null
    else isEquals(expected, actual)

  private def isEquals(expected: AnyRef, actual: AnyRef): Boolean =
    expected == actual

  def assertEquals(expected: AnyRef, actual: AnyRef): Unit =
    assertEquals(null, expected, actual)

  def assertNotEquals(message: String, unexpected: AnyRef,
      actual: AnyRef): Unit = {
    if (equalsRegardingNull(unexpected, actual))
      failEquals(message, actual)
  }

  def assertNotEquals(unexpected: AnyRef, actual: AnyRef): Unit =
    assertNotEquals(null, unexpected, actual)

  private def failEquals(message: String, actual: AnyRef): Unit = {
    val checkedMessage = {
      if (message != null) message
      else "Values should be different"
    }
    fail(s"$checkedMessage. Actual: $actual")
  }

  def assertNotEquals(message: String, unexpected: Long, actual: Long): Unit = {
    if (unexpected == actual)
      failEquals(message, java.lang.Long.valueOf(actual))
  }

  def assertNotEquals(unexpected: Long, actual: Long): Unit =
    assertNotEquals(null, unexpected, actual)

  def assertNotEquals(message: String, unexpected: Double, actual: Double,
      delta: Double): Unit = {
    if (!doubleIsDifferent(unexpected, actual, delta))
      failEquals(message,  java.lang.Double.valueOf(actual))
  }

  def assertNotEquals(unexpected: Double, actual: Double, delta: Double): Unit =
    assertNotEquals(null, unexpected, actual, delta)

  def assertNotEquals(unexpected: Float, actual: Float, delta: Float): Unit =
    assertNotEquals(null, unexpected, actual, delta)

  def assertArrayEquals(message: String, expecteds: Array[AnyRef],
      actuals: Array[AnyRef]): Unit = {
    internalArrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[AnyRef],
      actuals: Array[AnyRef]): Unit = {
    assertArrayEquals(null, expecteds, actuals)
  }

  def assertArrayEquals(message: String, expecteds: Array[Boolean],
      actuals: Array[Boolean]): Unit = {
    internalArrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[Boolean],
      actuals: Array[Boolean]): Unit = {
    assertArrayEquals(null, expecteds, actuals)
  }

  def assertArrayEquals(message: String, expecteds: Array[Byte],
      actuals: Array[Byte]): Unit = {
    internalArrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[Byte], actuals: Array[Byte]): Unit =
    assertArrayEquals(null, expecteds, actuals)

  def assertArrayEquals(message: String, expecteds: Array[Char],
      actuals: Array[Char]): Unit = {
    internalArrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[Char], actuals: Array[Char]): Unit =
    assertArrayEquals(null, expecteds, actuals)

  def assertArrayEquals(message: String, expecteds: Array[Short],
      actuals: Array[Short]): Unit = {
    internalArrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[Short],
      actuals: Array[Short]): Unit = {
    assertArrayEquals(null, expecteds, actuals)
  }

  def assertArrayEquals(message: String, expecteds: Array[Int],
      actuals: Array[Int]): Unit = {
    internalArrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[Int], actuals: Array[Int]): Unit =
    assertArrayEquals(null, expecteds, actuals)

  def assertArrayEquals(message: String, expecteds: Array[Long],
      actuals: Array[Long]): Unit =  {
    internalArrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[Long], actuals: Array[Long]): Unit =
    assertArrayEquals(null, expecteds, actuals)

  def assertArrayEquals(message: String, expecteds: Array[Double],
      actuals: Array[Double], delta: Double): Unit = {
    new InexactComparisonCriteria(delta).arrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[Double], actuals: Array[Double],
      delta: Double): Unit = {
    assertArrayEquals(null, expecteds, actuals, delta)
  }

  def assertArrayEquals(message: String, expecteds: Array[Float],
      actuals: Array[Float], delta: Float): Unit = {
    new InexactComparisonCriteria(delta).arrayEquals(message, expecteds, actuals)
  }

  def assertArrayEquals(expecteds: Array[Float], actuals: Array[Float],
      delta: Float): Unit = {
    assertArrayEquals(null, expecteds, actuals, delta)
  }

  private def internalArrayEquals(message: String, expecteds: AnyRef,
      actuals: AnyRef): Unit = {
    new ExactComparisonCriteria().arrayEquals(message, expecteds, actuals)
  }

  def assertEquals(message: String, expected: Double, actual: Double,
      delta: Double): Unit = {
    if (doubleIsDifferent(expected, actual, delta)) {
      failNotEquals(message, java.lang.Double.valueOf(expected),
        java.lang.Double.valueOf(actual))
    }
  }

  def assertEquals(message: String, expected: Float, actual: Float, delta: Float) {
    if (floatIsDifferent(expected, actual, delta)) {
      failNotEquals(message, java.lang.Float.valueOf(expected),
          java.lang.Float.valueOf(actual))
    }
  }

  def assertNotEquals(message: String, unexpected: Float, actual: Float,
      delta: Float): Unit = {
    if (!floatIsDifferent(unexpected, actual, delta))
      failEquals(message, java.lang.Float.valueOf(actual))
  }

  private def doubleIsDifferent(d1: Double, d2: Double,
      delta: Double): Boolean = {
    java.lang.Double.compare(d1, d2) != 0 && Math.abs(d1 - d2) > delta
  }

  private def floatIsDifferent(f1: Float, f2: Float, delta: Float): Boolean =
    java.lang.Float.compare(f1, f2) != 0 && Math.abs(f1 - f2) > delta

  def assertEquals(expected: Long, actual: Long): Unit =
    assertEquals(null, expected, actual)

  def assertEquals(message: String, expected: Long, actual: Long): Unit = {
    if (expected != actual) {
      failNotEquals(message, java.lang.Long.valueOf(expected),
          java.lang.Long.valueOf(actual))
    }
  }

//  @deprecated
//  def assertEquals(expected: Double, actual: Double) {
//    assertEquals(null, expected, actual)
//  }

//  @deprecated
//  def assertEquals(message: String, expected: Double, actual: Double) {
//    fail("Use assertEquals(expected, actual, delta) to compare floating-point numbers")
//  }

  def assertEquals(expected: Double, actual: Double, delta: Double): Unit =
    assertEquals(null, expected, actual, delta)

  def assertEquals(expected: Float, actual: Float, delta: Float): Unit =
    assertEquals(null, expected, actual, delta)

  def assertNotNull(message: String, obj: AnyRef): Unit =
    assertTrue(message, obj != null)

  def assertNotNull(obj: AnyRef): Unit =
    assertNotNull(null, obj)

  def assertNull(message: String, obj: AnyRef): Unit =
    if (obj != null) failNotNull(message, obj)

  def assertNull(obj: AnyRef): Unit =
    assertNull(null, obj)

  private def failNotNull(message: String, actual: AnyRef): Unit = {
    val formatted = if (message != null) message + " " else ""
    fail(s"${formatted}expected null, but was:<${actual}>")
  }

  def assertSame(message: String, expected: AnyRef, actual: AnyRef): Unit =
    if (expected ne actual) failNotSame(message, expected, actual)

  def assertSame(expected: AnyRef, actual: AnyRef): Unit =
    assertSame(null, expected, actual)

  def assertNotSame(message: String, unexpected: AnyRef, actual: AnyRef): Unit =
    if (unexpected eq actual) failSame(message)

  def assertNotSame(unexpected: AnyRef, actual: AnyRef): Unit =
    assertNotSame(null, unexpected, actual)

  private def failSame(message: String): Unit =
    if (message == null) fail( "expected not same")
    else fail(s"$message expected not same")

  private def failNotSame(message: String, expected: AnyRef,
      actual: AnyRef): Unit = {
    if (message == null)
      fail(s"expected same:<$expected> was not:<$actual>")
    else
      fail(s"$message expected same:<$expected> was not:<$actual>")
  }

  private def failNotEquals(message: String, expected: AnyRef,
      actual: AnyRef): Unit = {
    fail(format(message, expected, actual))
  }

  private[junit] def format(message: String, expected: AnyRef,
      actual: AnyRef): String = {
    val formatted = if (message != null && message != "") message + " " else ""
    val expectedString = String.valueOf(expected)
    val actualString = String.valueOf(actual)
    if (expectedString == actualString) {
      val expectedFormatted = formatClassAndValue(expected, expectedString)
      val actualFormatted = formatClassAndValue(actual, actualString)
      s"${formatted}expected: $expectedFormatted but was: $actualFormatted"
    } else {
      s"${formatted}expected:<$expectedString> but was:<$actualString>"
    }
  }

  private def formatClassAndValue(value: AnyRef, valueString: String): String = {
    val className = if (value == null) "null" else value.getClass.getName
    s"$className<$valueString>"
  }

//  @deprecated
//  def assertEquals(message: String, expecteds: Array[AnyRef], actuals: Array[AnyRef]) {
//    assertArrayEquals(message, expecteds, actuals)
//  }

//  @deprecated
//  def assertEquals(expecteds: Array[AnyRef], actuals: Array[AnyRef]) {
//    assertArrayEquals(expecteds, actuals)
//  }

  def assertThat[T](actual: T, matcher: Matcher[T]): Unit =
    assertThat("", actual, matcher)

  def assertThat[T](reason: String, actual: T, matcher: Matcher[T]): Unit =
    MatcherAssert.assertThat(reason, actual, matcher)

  def assertThrows(expectedThrowable: Class[_ <: Throwable],
      runnable: ThrowingRunnable): Unit = {
    expectThrows(expectedThrowable, runnable)
  }

  def expectThrows[T <: Throwable](expectedThrowable: Class[T], runnable: ThrowingRunnable): T = {
    try {
      runnable.run()
    } catch {
      case actualThrown: Throwable =>
        if (expectedThrowable.isInstance(actualThrown)) {
          actualThrown.asInstanceOf[T]
        } else {
          val mismatchMessage = format("unexpected exception type thrown;",
            expectedThrowable.getSimpleName, actualThrown.getClass.getSimpleName)

          val assertionError = new AssertionError(mismatchMessage)
          assertionError.initCause(actualThrown)
          throw assertionError
        }
    }
    val message = s"expected ${expectedThrowable.getSimpleName} to be thrown," +
        s" but nothing was thrown"
    throw new AssertionError(message)
  }

  trait ThrowingRunnable {
    def run(): Unit
  }
}
