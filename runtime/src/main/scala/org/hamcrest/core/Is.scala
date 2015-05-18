package org.hamcrest.core

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

import org.hamcrest.core.IsEqual.equalTo
import  org.hamcrest.core.IsInstanceOf.instanceOf

/**
 * Decorates another Matcher, retaining the behaviour but allowing tests
 * to be slightly more expressive.
 *
 * For example:  assertThat(cheese, equalTo(smelly))
 *          vs.  assertThat(cheese, is(equalTo(smelly)))
 */
class Is[T] (
     matcher: Matcher[T]
  )extends BaseMatcher[T] {

  override def matches(arg: AnyRef): Boolean =
    matcher.matches(arg)

  override def describeTo(description: Description) {
    description.appendText("is ").appendDescriptionOf(matcher)
  }

  override def describeMismatch(item: AnyRef, mismatchDescription: Description) {
    matcher.describeMismatch(item, mismatchDescription)
  }
}

object Is {

    /**
     * Decorates another Matcher, retaining its behaviour, but allowing tests
     * to be slightly more expressive.
     * For example:
     * <pre>assertThat(cheese, is(equalTo(smelly)))</pre>
     * instead of:
     * <pre>assertThat(cheese, equalTo(smelly))</pre>
     *
     */
    def is[T](matcher: Matcher[T]): Matcher[T] =
      new Is[T](matcher)

    /**
     * A shortcut to the frequently used <code>is(equalTo(x))</code>.
     * For example:
     * <pre>assertThat(cheese, is(smelly))</pre>
     * instead of:
     * <pre>assertThat(cheese, is(equalTo(smelly)))</pre>
     *
     */
    def is[T](value: T): Matcher[T] =
      is(equalTo(value))


    /**
     * A shortcut to the frequently used <code>is(instanceOf(SomeClass.class))</code>.
     * For example:
     * <pre>assertThat(cheese, isA(Cheddar.class))</pre>
     * instead of:
     * <pre>assertThat(cheese, is(instanceOf(Cheddar.class)))</pre>
     *
     */
    def isA[T](typ: Class[T]): Matcher[T] = {
      val typeMatcher: Matcher[T] = instanceOf(typ)
      is(typeMatcher)
    }
}