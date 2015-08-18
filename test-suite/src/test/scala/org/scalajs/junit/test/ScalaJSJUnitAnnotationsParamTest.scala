package org.scalajs.junit.test

import org.junit.Test

class ScalaJSJUnitAnnotationsParamTest {
  @Test
  def test0(): Unit = { }

  @Test(expected = classOf[Exception])
  def testException(): Unit =
    throw new Exception("error message")

  @Test(expected = classOf[Exception])
  def testException2(): Unit =
    throw new IndexOutOfBoundsException("error message")

  // Doesn't work yet, but useless as timeout can't be handled properly
  //  @Test(timeout = 0L)
  //  def testTimeOut0(): Unit = { }

  // Doesn't work yet, but useless as timeout can't be handled properly
  //  @Test(timeout = 10000L)
  //  def testTimeOut1(): Unit = { }

  @Test(expected = classOf[Exception], timeout = 10000L)
  def test3(): Unit = throw new Exception

  @Test def t: Unit = throw new Exception("dsaf")
}
