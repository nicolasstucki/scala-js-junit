package org.hamcrest

/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */

object MatcherAssert {
  def assertThat[T<:AnyRef](actual: T, matcher: Matcher[T]) {
      assertThat("", actual, matcher)
  }

  def assertThat[T<:AnyRef](reason: String, actual: T, matcher: Matcher[T]) {
    throw new UnsupportedOperationException("assertThat is still not supported in scala.js")
  }

  def assertThat(reason: String, assertion: Boolean) {
    if (!assertion)
      throw new AssertionError(reason);
  }
}