package org.scalajs.junit.test

import org.junit._
import org.junit.runners.MethodSorters

object ScalaJSJUnitAnnotationsTest {
  @BeforeClass
  def beforeClassTest(): Unit =
    println(s"ScalaJSJUnitAnnotationTest.beforeClassTest()")

  @AfterClass
  def afterClassTest(): Unit =
    println(s"ScalaJSJUnitAnnotationTest.afterClassTest()")
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ScalaJSJUnitAnnotationsTest {
  @Before
  def beforeTest(): Unit =
    println(s"ScalaJSJUnitAnnotationTest.beforeTest()")

  @After
  def afterTest(): Unit =
    println(s"ScalaJSJUnitAnnotationTest.afterTest()")

  @Test
  def test1(): Unit =
    println(s"ScalaJSJUnitAnnotationTest.test1()")

  @Test
  def test2(): Unit =
    println(s"ScalaJSJUnitAnnotationTest.test2()")

  @Test
  def test3(): Unit =
    println(s"ScalaJSJUnitAnnotationTest.test3()")

  @Test(timeout = 1L)
  def testTimeOut(): Unit = {
    println(s"ScalaJSJUnitAnnotationTest.testTimeOut() start")
    (1 to 10000).foreach(_ => new Object)
    println(s"ScalaJSJUnitAnnotationTest.testTimeOut() end")
  }

  @Test(timeout = Long.MaxValue)
  def testNoTimeOut(): Unit =
    println(s"ScalaJSJUnitAnnotationTest.test3()")

  @Test(expected = classOf[Exception])
  def testExpectedException(): Unit =
    throw new Exception

  @Test(expected = classOf[IndexOutOfBoundsException])
  def testExpectedIndexOutOfBoundsException(): Unit =
    throw new IndexOutOfBoundsException

  @Ignore
  @Test
  def testIgnore(): Unit = { }

  @Ignore("This is the @Ignore message")
  @Test
  def testIgnoreWithMessage(): Unit = { }
}
