package org.scalajs.junit.framework

import scala.scalajs.js.annotation.JSExportDescendentClasses

@JSExportDescendentClasses
trait ScalaJSJUnitTest {
  /**
   * List of all test methods in the class
   */
  def listTestMethods(): List[ScalaJSJUnitTest.TestMethod] = Nil
}

object ScalaJSJUnitTest {
  type TestMethod = String
}
