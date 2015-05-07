package org.scalajs.junit.test

import org.scalajs.junit.framework.ScalaJSJUnitTest
import org.junit.Assert._
// import org.junit.Test

class ScalaJSJUnitAnnotationTest extends ScalaJSJUnitTest {

  // @Test
  def testAssertTrue() = {
    assertTrue("'true' did not assertTrue", true)
  }

   // @Test
  def testAssertFalse() = {
    assertFalse("'false' did not assertFalse", false)
  }

  override def listTestMethods(): List[ScalaJSJUnitTest.TestMethod] = {
    return List("testAssertTrue", "testAssertFalse")
  }

}
