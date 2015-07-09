package org.scalajs.junit.test

import org.junit._

import org.junit.Assert._
import org.junit.Assume._
import org.junit.runners.MethodSorters
import org.scalajs.junit._
import org.junit.AssumptionViolatedException
import org.hamcrest.CoreMatchers._

import scala.scalajs.js.annotation.JSExport

object ScalaJSJUnitAnnotationTest extends ScalaJSJUnitTest {
  @BeforeClass
  def beforeClassTest1() {
    println(s"ScalaJSJUnitAnnotationTest.beforeClassTest1()")
  }

  @BeforeClass
  def beforeClassTest2() {
    println(s"ScalaJSJUnitAnnotationTest.beforeClassTest2()")
  }

  @AfterClass
  def afterClassTest1() {
    println(s"ScalaJSJUnitAnnotationTest.afterClassTest1()")
  }

  @AfterClass
  def afterClassTest2() {
    println(s"ScalaJSJUnitAnnotationTest.afterClassTest2()")
  }

  def invokeJUnitMethod$ (methodId: String): Unit = {
    if (methodId == "0") beforeClassTest1()
    else if (methodId == "1") beforeClassTest2()
    else if (methodId == "2") afterClassTest1()
    else if (methodId == "3") afterClassTest2()
    else throw new NoSuchMethodException()
  }

  def getJUnitMetadata$ (): TestClass = {
    TestClass(
      List(),
      List(
        AnnotatedMethod("beforeClassTest1", "0", List(new BeforeClass)),
        AnnotatedMethod("beforeClassTest2", "1", List(new BeforeClass)),
        AnnotatedMethod("afterClassTest1", "2", List(new AfterClass)),
        AnnotatedMethod("afterClassTest2", "3", List(new AfterClass)))
    )
  }

  override def toString: String = "MODULE FOUND"
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ScalaJSJUnitAnnotationTest extends ScalaJSJUnitTest {

  private val notEquals = false
  private val shallNotPass = false

  def testIf(code: =>Unit, shouldPass: Boolean, msg: String) {
    try {
      code
      if(!shouldPass)
        fail(msg)
    } catch {
      case assErr: AssertionError =>
        if(shouldPass)
          throw assErr
    }
  }

  def testIfAsserts(assertion: =>Unit, shouldPass: Boolean = true) {
    try {
      assertion
      if(!shouldPass)
        fail("Assertion should have failed")
    } catch {
      case assErr: AssertionError =>
        if(shouldPass)
          throw assErr
    }
  }

  def testIfAssumePass(assumption: =>Unit, shouldPass: Boolean = true) {
    try {
      assumption
      if(!shouldPass)
        fail("Assumption should have failed")
    } catch {
      case assVio: AssumptionViolatedException[_] =>
        if(shouldPass)
          throw assVio
    }
  }

  @Before
  def beforeTest1() {
    println(s"ScalaJSJUnitAnnotationTest.beforeTest1()")
  }


  @Before
  def beforeTest2() {
    println(s"ScalaJSJUnitAnnotationTest.beforeTest2()")
  }

  @After
  def afterTest1() {
    println(s"ScalaJSJUnitAnnotationTest.afterTest1()")
  }


  @After
  def afterTest2() {
    println(s"ScalaJSJUnitAnnotationTest.afterTest2()")
  }

  @Test
  def testAssertTrueFalse() = {
    testIfAsserts(assertTrue("'true' did not assertTrue", condition = true))
    testIfAsserts(assertTrue(true))

    testIfAsserts(assertFalse("'false' did not assertFalse", condition = false))
    testIfAsserts(assertFalse(false))

    testIfAsserts(assertTrue("'true' did not assertTrue", condition = false), shallNotPass)
    testIfAsserts(assertTrue(false), shallNotPass)

    testIfAsserts(assertFalse("'false' did not assertFalse", condition = true), shallNotPass)
    testIfAsserts(assertFalse(true), shallNotPass)
  }

  @Test
  def testAssertNull() = {
	  testIfAsserts(assertNull("'null' did not assertNull", null))
    testIfAsserts(assertNull(null))

    testIfAsserts(assertNotNull("'new Object' did not assertNotNull", new Object))
    testIfAsserts(assertNotNull(new Object))

    testIfAsserts(assertNull("'null' did not assertNull", new Object), shallNotPass)
    testIfAsserts(assertNull(new Object), shallNotPass)

    testIfAsserts(assertNotNull("'null' did not assertNotNull", null), shallNotPass)
    testIfAsserts(assertNotNull(null), shallNotPass)
  }

  @Test
  def testAssertSame() = {
    // Setup
    val obj = new Object()
    val str = "abcd"
    val nullRef: AnyRef = null

    def testAssertion(expected: AnyRef, actual: AnyRef, equals: Boolean = true) {
      testIfAsserts(assertSame("References where not equal", expected, actual), equals)
      testIfAsserts(assertSame(expected, actual), equals)
      testIfAsserts(assertNotSame("References where equal", expected, actual), !equals)
      testIfAsserts(assertNotSame(expected, actual), !equals)
    }

    // Tests
    testAssertion(obj, obj)
    testAssertion(str, str)
    testAssertion(nullRef, nullRef)

    testAssertion(new Object, new Object, notEquals)
  }

  @Test
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

  @Test
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
    testFloatAssertion(Array(1f, 2f, 3f), Array(1f, 2f, 4f), 1f)
    testFloatAssertion(Array(1f, 2f, 3f), Array(1f, 2f, 3.5f), 1f)

  }

  @Test
  def testAssertThat() {

    testIfAsserts(assertThat("42", instanceOf("".getClass)))
    testIfAsserts(assertThat("42", instanceOf(1.getClass)), shallNotPass)

    testIfAsserts(assertThat(42, instanceOf(345453353.getClass)))
    testIfAsserts(assertThat(42, instanceOf(1L.getClass)), shallNotPass)
    testIfAsserts(assertThat(42, instanceOf("".getClass)), shallNotPass)

    testIfAsserts(assertThat(Float.MaxValue, instanceOf(0f.getClass)))
    testIfAsserts(assertThat(Double.MaxValue, instanceOf(0d.getClass)))

    testIfAsserts(assertThat(0, instanceOf(0d.getClass)))

  }

  @Test
  def testAssumeTrue() {
    testIfAssumePass(assumeTrue("true be assumed to be true", b = true))
    testIfAssumePass(assumeTrue(true))
    testIfAssumePass(assumeTrue("false be assumed to be true", b = false), shallNotPass)
    testIfAssumePass(assumeTrue( false), shallNotPass)

    testIfAssumePass(assumeFalse("false be assumed to be false", b = false))
    testIfAssumePass(assumeFalse(false))
    testIfAssumePass(assumeFalse("true be assumed to be false", b = true), shallNotPass)
    testIfAssumePass(assumeFalse(true), shallNotPass)

  }

  @Test
  def testAssumeNotNull() {
    testIfAssumePass(assumeNotNull())
    testIfAssumePass(assumeNotNull(new Object))
    testIfAssumePass(assumeNotNull("", new Object, " "))

    testIfAssumePass(assumeNotNull(null), shallNotPass)
    testIfAssumePass(assumeNotNull(new Object, null), shallNotPass)
    testIfAssumePass(assumeNotNull(null, new Object), shallNotPass)
  }

  @Test
  def testAssumeThat() {
    testIfAssumePass(assumeThat(null, nullValue()))
    testIfAssumePass(assumeThat(null, notNullValue()), shallNotPass)

    testIfAssumePass(assumeThat(new Object, notNullValue()))
    testIfAssumePass(assumeThat(new Object, nullValue()), shallNotPass)

    testIfAssumePass(assumeThat(new Object, notNullValue("".getClass)))

    testIfAssumePass(assumeThat(1, is(1)))
    testIfAssumePass(assumeThat(1, is(2)), shallNotPass)

    testIfAssumePass(assumeThat(1, not(is(2))))
    testIfAssumePass(assumeThat(1, not(is(1))), shallNotPass)

    testIfAssumePass(assumeThat(1, is(not(2))))
    testIfAssumePass(assumeThat(1, is(not(1))), shallNotPass)

    testIfAssumePass(assumeThat(1, not(2)))
    testIfAssumePass(assumeThat(1, not(1)), shallNotPass)
  }

  @Test
  def testAssumesNoException(): Unit = {
    testIfAssumePass(assumeNoException("assumeNoException(null) should succeed", null))
    testIfAssumePass(assumeNoException(null))

    testIfAssumePass(assumeNoException("assumeNoException(new Throwable) should succeed", new Throwable), shallNotPass)
    testIfAssumePass(assumeNoException(new Throwable), shallNotPass)
  }

  @Ignore
  @Test
  def testIgnore0(): Unit = { }

  @Ignore("Ignore message")
  @Test
  def testIgnore1(): Unit = { }

  @JSExport
  def exportedMethod: Unit = { }

  def invokeJUnitMethod$ (methodId: String): Unit = {
    if (methodId == "0") beforeTest1()
    else if (methodId == "1") beforeTest2()
    else if (methodId == "2") testAssertTrueFalse()
    else if (methodId == "3") testAssertThat()
    else if (methodId == "4") afterTest1()
    else if (methodId == "5") afterTest2()
    else if (methodId == "6") testIgnore0()
    else if (methodId == "7") testIgnore1()
    else if (methodId == "8") testAssertEquals()
    else if (methodId == "9") testAssertNull()
    else throw new NoSuchMethodException()
  }

  def getJUnitMetadata$ (): TestClass = {
    TestClass(
      List(new FixMethodOrder(MethodSorters.NAME_ASCENDING)),
      List(
        AnnotatedMethod("beforeTest1", "0", List(new Before)),
        AnnotatedMethod("beforeTest2", "1", List(new Before)),
        AnnotatedMethod("testAssertTrueFalse", "2", List(new Test)),
        AnnotatedMethod("testAssertThat", "3", List(new Test)),
        AnnotatedMethod("afterTest1", "4", List(new After)),
        AnnotatedMethod("afterTest2", "5", List(new After)),
        AnnotatedMethod("testIgnore0", "6", List(new Test, new Ignore)),
        AnnotatedMethod("testIgnore1", "7", List(new Test, new Ignore("Ignore message"))),
        AnnotatedMethod("testAssertEquals", "8", List(new Test)),
        AnnotatedMethod("testAssertNull", "9", List(new Test))
      )
    )
  }
}
