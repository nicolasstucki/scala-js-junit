package org.hamcrest.core

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

import org.hamcrest.core.IsNot.not

/**
 * Is the value null?
 */
class IsNull[T<:AnyRef] extends BaseMatcher[T] {
    override def matches(o: AnyRef): Boolean = {
      o == null
    }

    override def describeTo(description: Description) {
        description.appendText("null")
    }
}

object IsNull {

  /**
   * Creates a matcher that matches if examined object is <code>null</code>.
   * For example:
   * <pre>assertThat(cheese, is(nullValue())</pre>
   *
   */
  def nullValue(): Matcher[AnyRef] =
    new IsNull[AnyRef]


  /**
   * A shortcut to the frequently used <code>not(nullValue())</code>.
   * For example:
   * <pre>assertThat(cheese, is(notNullValue()))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(nullValue())))</pre>
   *
   */
  def notNullValue(): Matcher[AnyRef] =
    not(nullValue())


  /**
   * Creates a matcher that matches if examined object is <code>null</code>. Accepts a
   * single dummy argument to facilitate type inference.
   * For example:
   * <pre>assertThat(cheese, is(nullValue(Cheese.class))</pre>
   *
   * @param type
   *     dummy parameter used to infer the generic type of the returned matcher
   */
  def nullValue[T<:AnyRef](tpe: Class[T]): Matcher[T] =
      new IsNull[T]()

  /**
   * A shortcut to the frequently used <code>not(nullValue(X.class)). Accepts a
   * single dummy argument to facilitate type inference.</code>.
   * For example:
   * <pre>assertThat(cheese, is(notNullValue(X.class)))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(nullValue(X.class))))</pre>
   *
   * @param type
   *     dummy parameter used to infer the generic type of the returned matcher
   *
   */
  def notNullValue[T<:AnyRef](typ: Class[T]): Matcher[T] =
    not(nullValue(typ))
}