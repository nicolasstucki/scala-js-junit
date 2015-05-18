package org.hamcrest.core

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

import org.hamcrest.core.IsEqual.equalTo


/**
 * Calculates the logical negation of a matcher.
 */
class IsNot[T](
    matcher: Matcher[T]
  ) extends BaseMatcher[T] {

  override def matches(arg: AnyRef): Boolean =
    !matcher.matches(arg)


  override def describeTo(description: Description) {
    description.appendText("not ").appendDescriptionOf(matcher)
  }

}

object IsNot {

  /**
   * Creates a matcher that wraps an existing matcher, but inverts the logic by which
   * it will match.
   * For example:
   * <pre>assertThat(cheese, is(not(equalTo(smelly))))</pre>
   *
   * @param matcher
   *     the matcher whose sense should be inverted
   */
  def not[T](matcher: Matcher[T]): Matcher[T] =
    new IsNot[T](matcher)

  /**
   * A shortcut to the frequently used <code>not(equalTo(x))</code>.
   * For example:
   * <pre>assertThat(cheese, is(not(smelly)))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(equalTo(smelly))))</pre>
   *
   * @param value
   *     the value that any examined object should <b>not</b> equal
   */
  def not[T](value: T): Matcher[T] =
    not(equalTo(value))

}