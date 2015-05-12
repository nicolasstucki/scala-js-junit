package org.scalajs.junit.test

import org.scalajs.junit.Test
import org.junit.Assert._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportDescendentClasses
import scala.util.Try

class ScalaJSJUnitAnnotationTest extends Test {

  private val notEquals = false

  def testIfAsserts(assetion: =>Unit, shouldPass: Boolean) {
    try {
      assetion
      if(!shouldPass)
        fail("Should have failed")
    } catch {
      case assErr: AssertionError =>
        if(shouldPass)
          throw assErr
    }
  }

  // @Test
  def testAssertTrueFalse() = {
    assertTrue("'true' did not assertTrue", true)
    assertTrue(true)

    assertFalse("'false' did not assertFalse", false)
    assertFalse(false)
  }

  // @Test
  def testAssertNull() = {
	  assertNull("'null' did not assertNull", null)
    assertNull(null)

    assertNotNull("'new Object' did not assertFalse", new Object)
    assertNotNull(new Object)
    assertNotNull("")
  }

  // @Test
  def testAssertSame() = {
    // Setup
    val obj = new Object()
    val str = "abcd"
    val nullRef: AnyRef = null

    def testAssertion(expected: AnyRef, actual: AnyRef, equals: Boolean = true) {
      testIfAsserts(assertSame("Refferences where not equal", expected, actual), equals)
      testIfAsserts(assertSame(expected, actual), equals)
      testIfAsserts(assertNotSame("Refferences where equal", expected, actual), !equals)
      testIfAsserts(assertNotSame(expected, actual), !equals)
    }

    // Tests
    testAssertion(obj, obj)
    testAssertion(str, str)
    testAssertion(nullRef, nullRef)

    testAssertion(new Object, new Object, notEquals)
  }

  // @Test
  def testAssertEquals() = {

    // Setup
    val obj = new Object()
    val str = "abcd"
    val nullRef: AnyRef = null


    // Object equality tests
    def testAssertion(expected: AnyRef, actual: AnyRef, equals: Boolean = true) {
      testIfAsserts(assertEquals(s"Asserting $expected == $actual", expected, actual), equals)
      testIfAsserts(assertEquals(expected, actual), equals)
      testIfAsserts(assertNotEquals(s"Asserting $expected != $actual", expected, actual), !equals)
      testIfAsserts(assertNotEquals(expected, actual), !equals)
    }

    testAssertion(nullRef, nullRef)
    testAssertion(obj, obj)
    testAssertion(str, str)
    testAssertion(new Object, null, notEquals)
    testAssertion(null, new Object, notEquals)
    testAssertion(new Object, new Object, notEquals)

    testAssertion("", "")
    testAssertion("42", "42")
    testAssertion("asdfasfsafs", "asdfasfsafs")
    testAssertion(List(1, 2, 3), List(1, 2, 3))
    testAssertion(List(1L, 2L, 3L), List(1L, 2L, 3L))
    testAssertion(Vector(1L, 2L, 3L), List(1L, 2L, 3L))
    testAssertion((1, 2, 3), (1, 2, 3))
    testAssertion("", "d", equals = false)
    testAssertion("42", "1", equals = false)
    testAssertion("asdfasfsafs", "asdfuhafs", notEquals)
    testAssertion(List(1, 2, 3), List(1, 2, 4), notEquals)
    testAssertion(List(1L, 2L, 3L), List(1L, 2L, 4L), notEquals)
    testAssertion(Vector(1L, 2L, 3L), List(1L, 2L, 4L), notEquals)
    testAssertion((1, 2, 3), (1, 2, 4), notEquals)


    // Byte equality tests
    def testByteAssertion(expected: Byte, actual: Byte, equals: Boolean = true) {
      testIfAsserts(assertEquals(s"Asserting $expected == $actual", expected, actual), equals)
      testIfAsserts(assertEquals(expected, actual), equals)
      testIfAsserts(assertNotEquals(s"Asserting $expected != $actual", expected, actual), !equals)
      testIfAsserts(assertNotEquals(expected, actual), !equals)
    }
    testByteAssertion(0, 0)
    testByteAssertion(42, 42)
    testByteAssertion(-42, -42)
    testByteAssertion(Byte.MinValue, Byte.MinValue)
    testByteAssertion(Byte.MaxValue, Byte.MaxValue)
    testByteAssertion(1, 2, notEquals)


    // Char equality tests
    def testCharAssertion(expected: Char, actual: Char, equals: Boolean = true) {
      testIfAsserts(assertEquals(s"Asserting $expected == $actual", expected, actual), equals)
      testIfAsserts(assertEquals(expected, actual), equals)
      testIfAsserts(assertNotEquals(s"Asserting $expected != $actual", expected, actual), !equals)
      testIfAsserts(assertNotEquals(expected, actual), !equals)
    }

    testCharAssertion('a', 'a')
    testCharAssertion('@', '@')
    testCharAssertion('\n', '\n')
    testCharAssertion('a', null.asInstanceOf[Char], notEquals)
    testCharAssertion('a', '@', notEquals)
    testCharAssertion('a', '\n', notEquals)


    // Short equality tests
    def testShortAssertion(expected: Short, actual: Short, equals: Boolean = true) {
      testIfAsserts(assertEquals(s"Asserting $expected == $actual", expected, actual), equals)
      testIfAsserts(assertEquals(expected, actual), equals)
      testIfAsserts(assertNotEquals(s"Asserting $expected != $actual", expected, actual), !equals)
      testIfAsserts(assertNotEquals(expected, actual), !equals)
    }
    testShortAssertion(0, 0)
    testShortAssertion(42, 42)
    testShortAssertion(-42, -42)
    testShortAssertion(Short.MinValue, Short.MinValue)
    testShortAssertion(Short.MaxValue, Short.MaxValue)
    testShortAssertion(1, 2, notEquals)


    // Int equality tests
    def testIntAssertion(expected: Int, actual: Int, equals: Boolean = true) {
      testIfAsserts(assertEquals(s"Asserting $expected == $actual", expected, actual), equals)
      testIfAsserts(assertEquals(expected, actual), equals)
      testIfAsserts(assertNotEquals(s"Asserting $expected != $actual", expected, actual), !equals)
      testIfAsserts(assertNotEquals(expected, actual), !equals)
    }

    testIntAssertion(0, 0)
    testIntAssertion(42, 42)
    testIntAssertion(-42, -42)
    testIntAssertion(Int.MinValue, Int.MinValue)
    testIntAssertion(Int.MaxValue, Int.MaxValue)
    testIntAssertion(1, 2, notEquals)


    // Long equality tests
    def testLongAssertion(expected: Long, actual: Long, equals: Boolean = true) {
      testIfAsserts(assertEquals(s"Asserting $expected == $actual", expected, actual), equals)
      testIfAsserts(assertEquals(expected, actual), equals)
      testIfAsserts(assertNotEquals(s"Asserting $expected != $actual", expected, actual), !equals)
      testIfAsserts(assertNotEquals(expected, actual), !equals)
    }

    testLongAssertion(0L, 0L)
    testLongAssertion(42L, 42L)
    testLongAssertion(-42L, -42L)
    testLongAssertion(Long.MinValue, Long.MinValue)
    testLongAssertion(Long.MaxValue, Long.MaxValue)
    testLongAssertion(1L, 2L, notEquals)


    // Double equality tests
    def testDoubleAssertion(expected: Double, actual: Double, delta: Double, equals: Boolean = true) {
      testIfAsserts(assertEquals(s"Asserting $expected == $actual", expected, actual, delta), equals)
      testIfAsserts(assertEquals(expected, actual, delta), equals)
      testIfAsserts(assertNotEquals(s"Asserting $expected != $actual", expected, actual, delta), !equals)
      testIfAsserts(assertNotEquals(expected, actual, delta), !equals)
    }

    testDoubleAssertion(1d, 1d, 0d)
    testDoubleAssertion(1d, 2d, 1d)
    testDoubleAssertion(1d, 2d, 10d)
    testDoubleAssertion(1d, 1.1d, 0.2d)
    testDoubleAssertion(1d, 2d, 0d, notEquals)
    testDoubleAssertion(1d, 2d, 0.5d, notEquals)
    // TODO add tests for boundary values like nan, infinity, ...

    // Float equality tests
    def testFloatAssertion(expected: Float, actual: Float, delta: Float, equals: Boolean = true) {
      testIfAsserts(assertEquals(s"Asserting $expected == $actual", expected, actual, delta), equals)
      testIfAsserts(assertEquals(expected, actual, delta), equals)
      testIfAsserts(assertNotEquals(s"Asserting $expected != $actual", expected, actual, delta), !equals)
      testIfAsserts(assertNotEquals(expected, actual, delta), !equals)
    }

    testFloatAssertion(1f, 1f, 0f)
    testFloatAssertion(1f, 2f, 1f)
    testFloatAssertion(1f, 2f, 10f)
    testFloatAssertion(1f, 1.1f, 0.2f)
    testFloatAssertion(1f, 2f, 0f, notEquals)
    testFloatAssertion(1f, 2f, 0.5f, notEquals)
    // TODO add tests for boundary values like nan, infinity, ...

  }

  // @Test
  def testAssertArrayEquals() {
    // setup
    val (obj1, obj2) = ("0", "1")
    val arr1 = Array(obj1)

    val message = "Should be different up to != operator"

    def testAssertion[@specialized(AnyRef, Boolean, Byte, Char, Short, Int, Long) T](
        expected: Array[T], actual: Array[T], equals: Boolean = true) {
      testIfAsserts(assertArrayEquals(message, expected, actual), equals)
      testIfAsserts(assertArrayEquals(expected, actual), equals)
    }
    def testDoubleAssertion(expected: Array[Double], actual: Array[Double], delta: Double, equals: Boolean = true) {
      testIfAsserts(assertArrayEquals(message, expected, actual, delta), equals)
      testIfAsserts(assertArrayEquals(expected, actual, delta), equals)
    }
    def testFloatAssertion(expected: Array[Float], actual: Array[Float], delta: Float, equals: Boolean = true) {
      testIfAsserts(assertArrayEquals(message, expected, actual, delta), equals)
      testIfAsserts(assertArrayEquals(expected, actual, delta), equals)
    }

    // Array tests
    testAssertion(arr1, arr1)
    testAssertion(Array(obj1), Array(obj1))
    testAssertion(Array(obj1, obj2, obj2), Array(obj1, obj2, obj2))
    testAssertion(Array(obj1), Array("0"))
    testAssertion(Array(1, 2, 3), Array(1, 2, 3))
    testAssertion(Array(1L, 2L, 3L), Array(1L, 2L, 3L))
    testAssertion(Array(1d, 2d, 3d), Array(1d, 2d, 3d))
    testAssertion(Array(1f, 2f, 3f), Array(1f, 2f, 3f))
    testAssertion(Array(Array(1), Array(2, Array(3))), Array(Array(1), Array(2, Array(3))))

    testAssertion(Array(obj1), Array(obj2), notEquals)
    testAssertion(Array(obj1, obj2, obj2), Array(obj1, obj2, obj1), notEquals)
    testAssertion(Array(obj1), Array("4"), notEquals)
    testAssertion(Array(1, 2, 3), Array(1, 3, 3), notEquals)
    testAssertion(Array(1L, 2L, 3L), Array(1L, 1L, 3L), notEquals)
    testAssertion(Array(1d, 2d, 3d), Array(2d, 2d, 3d), notEquals)
    testAssertion(Array(1f, 2f, 3f), Array(1f, 2f, 5f), notEquals)
    testAssertion(Array(Array(2), Array(2, Array(3))), Array(Array(1), Array(2, Array(3))), notEquals)
    testAssertion(Array(Array(1, 2), Array(2, Array(3))), Array(Array(1), Array(2, Array(3))), notEquals)
    testAssertion(Array(Array(1), Array(2, Array(3))), Array(Array(1, 4), Array(2, Array(3))), notEquals)

    // Array[Double]
    testDoubleAssertion(Array(1d, 2d, 3d), Array(1d, 2d, 4d), 1d)
    testDoubleAssertion(Array(1d, 2d, 3d), Array(1d, 2d, 3.5d), 1d)

    // Array[Float]
    testDoubleAssertion(Array(1f, 2f, 3f), Array(1f, 2f, 4f), 1f)
    testDoubleAssertion(Array(1f, 2f, 3f), Array(1f, 2f, 3.5f), 1f)

  }

  override def listTestMethods(): List[Test.TestMethod] = {
    return List(
        Test.TestMethod("testAssertTrueFalse", () => Try(testAssertTrueFalse())),
        Test.TestMethod("testAssertNull", () => Try(testAssertNull())),
        Test.TestMethod("testAssertSame", () => Try(testAssertSame())),
        Test.TestMethod("testAssertEquals", () => Try(testAssertEquals())),
        Test.TestMethod("testAssertArrayEquals", () => Try(testAssertArrayEquals()))
        )
  }

}
