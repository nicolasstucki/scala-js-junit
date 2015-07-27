/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit.internal

import org.junit.Assert

abstract class ComparisonCriteria {

  def arrayEquals(message: String, expecteds: AnyRef, actuals: AnyRef): Unit =
    arrayEquals(message, expecteds, actuals, true)

  private object Arrays {
    def deepEquals(a1: Array[AnyRef], a2: Array[AnyRef]): Boolean = {
      if ((a1 eq a2) || a1.sameElements(a2))
        true
      else if (a1 == null || a2 == null || a1.length != a1.length)
        false
      else {
        (a1 zip a2).forall {
          case (e1, e2) if (e1 eq e2) || e1 == e2 => true
          case (e1: Array[AnyRef], e2: Array[AnyRef]) => deepEquals(e1, e2)
          case _ => false
        }
      }
    }
  }

  private def arrayEquals(message: String, expecteds: AnyRef, actuals: AnyRef,
      outer: Boolean) {
    if (expecteds != actuals &&
        !Arrays.deepEquals(Array(expecteds), Array(actuals))) {

      val header = if (message == null) "" else s"$message: "

      val exceptionMessage = if (outer) header else ""
      val expectedsLength =
        assertArraysAreSameLength(expecteds, actuals, exceptionMessage)

      for (i <- 0 until expectedsLength) {
        val expected = get(expecteds, i)
        val actual = get(actuals, i)

        if (isArray(expected) && isArray(actual)) {
          try {
            arrayEquals(message, expected, actual, false)
          } catch {
            case e: ArrayComparisonFailure =>
              e.addDimension(i)
              throw e
            case e: AssertionError =>
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
    val actualsLength = actuals.asInstanceOf[Array[_]].length
    val expectedsLength = expecteds.asInstanceOf[Array[_]].length
    if (actualsLength != expectedsLength) {
      Assert.fail(header + "array lengths differed, expected.length=" +
          expectedsLength +" actual.length=" + actualsLength)
    }
    expectedsLength
  }

  private def get(arr: AnyRef, i: Int): AnyRef = {
    arr match {
      case arr: Array[AnyRef]  => arr(i)
      case arr: Array[Boolean] => arr(i).asInstanceOf[AnyRef]
      case arr: Array[Byte]    => arr(i).asInstanceOf[AnyRef]
      case arr: Array[Char]    => arr(i).asInstanceOf[AnyRef]
      case arr: Array[Short]   => arr(i).asInstanceOf[AnyRef]
      case arr: Array[Int]     => arr(i).asInstanceOf[AnyRef]
      case arr: Array[Long]    => arr(i).asInstanceOf[AnyRef]
      case arr: Array[Float]   => arr(i).asInstanceOf[AnyRef]
      case arr: Array[Double]  => arr(i).asInstanceOf[AnyRef]
      case _                   => throw new Exception("expected array")
    }
  }

  private def length(arr: AnyRef): Int = {
    arr match {
      case arr: Array[AnyRef]  => arr.length
      case arr: Array[Boolean] => arr.length
      case arr: Array[Byte]    => arr.length
      case arr: Array[Char]    => arr.length
      case arr: Array[Short]   => arr.length
      case arr: Array[Int]     => arr.length
      case arr: Array[Long]    => arr.length
      case arr: Array[Float]   => arr.length
      case arr: Array[Double]  => arr.length
    }
  }

  protected def assertElementsEqual(expected: AnyRef, actual: AnyRef): Unit
}
