package org.scalajs.junit.test

import org.scalajs.junit.Test
import org.junit.Assert._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportDescendentClasses
import scala.util.Try

class ScalaJSJUnitAnnotationTest extends Test {

  // @Test
  def testAssertTrue() = {
    assertTrue("'true' did not assertTrue", true)
  }

   // @Test
  def testAssertFalse() = {
    assertFalse("'false' did not assertFalse", false)
  }

  override def listTestMethods(): List[Test.TestMethod] = {
    return List(
        Test.TestMethod("testAssertTrue", () => Try(testAssertTrue())),
        Test.TestMethod("testAssertFalse", () => Try(testAssertFalse()))
        )
  }

}
