package org.scalajs.junit

import scala.scalajs.js.annotation.JSExportDescendentClasses
import scala.util.Try

@JSExportDescendentClasses
trait Test {
  /**
   * List of all test methods in the class
   */
  def listTestMethods(): List[Test.TestMethod] = Nil
}

object Test {
  final case class TestMethod(name: String, executor: Function0[Try[Unit]])
}
