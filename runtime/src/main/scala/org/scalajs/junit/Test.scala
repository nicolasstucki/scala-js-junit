package org.scalajs.junit

import scala.scalajs.js.annotation.JSExportDescendentClasses
import scala.util.Try

@JSExportDescendentClasses
trait Test {
  /**
   * List of all test methods in the class
   */
  def getJUnitDefinitions(): Test.Clazz = Test.Clazz()
}

object Test {

  @inline
  final def call(methodInvokation: =>Unit): MethodCaller = {
    () => Try(methodInvokation)
  }

  final type MethodCaller = Function0[Try[Unit]]

  final case class Clazz(
      beforeClassMethods: List[BeforeClassMethod] = Nil,
      beforeMethods: List[BeforeMethod] = Nil,
      testMethods: List[TestMethod] = Nil,
      afterMethods: List[AfterMethod] = Nil,
      afterClassMethods: List[AfterClassMethod] = Nil,
      timeout: Long = 0L,
      extendedClasses: List[Clazz] = Nil
  ) {

  }

  sealed trait Method {
    def name: String
    def invokeTry: MethodCaller
  }
  case class BeforeClassMethod(name: String, invokeTry: MethodCaller) extends Method
  case class BeforeMethod(name: String, invokeTry: MethodCaller) extends Method
  case class TestMethod(name: String, invokeTry: MethodCaller) extends Method
  case class AfterMethod(name: String, invokeTry: MethodCaller) extends Method
  case class AfterClassMethod(name: String, invokeTry: MethodCaller) extends Method


}
