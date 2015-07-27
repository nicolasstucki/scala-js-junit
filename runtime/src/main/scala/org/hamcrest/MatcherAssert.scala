/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */
package org.hamcrest

object MatcherAssert {
  def assertThat[T](actual: T, matcher: Matcher[T]) {
      assertThat("", actual, matcher)
  }

  def assertThat[T](reason: String, actual: T, matcher: Matcher[T]) {
    //throw new UnsupportedOperationException("assertThat is still not supported in scala.js")
    val _actual = actual.asInstanceOf[AnyRef]
    if (!matcher.matches(_actual)) {
      val description = new StringDescription
      description.appendText(s"$reason\nExpected: ")
                 .appendDescriptionOf(matcher)
                 .appendText("\n     but: ")
      matcher.describeMismatch(_actual, description)

      throw new AssertionError(description.toString)
    }
  }

  def assertThat(reason: String, assertion: Boolean) {
    if (!assertion)
      throw new AssertionError(reason)
  }
}
