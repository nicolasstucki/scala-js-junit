/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */
package org.hamcrest.core

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class IsEqual[T](expectedValue: AnyRef) extends BaseMatcher[T] {

  override def matches(actualValue: AnyRef): Boolean =
    IsEqual.areEqual(actualValue, expectedValue)

  override def describeTo(description: Description): Unit =
    description.appendValue(expectedValue)
}

object IsEqual {
  private[IsEqual] def areEqual(actual: AnyRef, expected: AnyRef): Boolean = {
    if (actual == null)
      expected == null
    else if (expected != null && actual.isInstanceOf[Array[_]])
      expected.isInstanceOf[Array[_]] &&
      actual.asInstanceOf[Array[_]].toList == expected.asInstanceOf[Array[_]].toList
    else
      actual.equals(expected)
  }

  def equalTo[T](operand: T): Matcher[T] =
    new IsEqual[T](operand.asInstanceOf[AnyRef])

  def equalToObject(operand: AnyRef): Matcher[AnyRef] =
    new IsEqual[AnyRef](operand)
}
