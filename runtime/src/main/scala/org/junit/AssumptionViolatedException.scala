/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit

import org.hamcrest.Matcher

// @SuppressWarnings("deprecation")
class AssumptionViolatedException[T] protected(fAssumption: String,
    fValueMatcher: Boolean, fMatcher: Matcher[_], fValue: AnyRef)
    extends org.junit.internal.AssumptionViolatedException(fAssumption,
        fValueMatcher, fMatcher, fValue) {

  def this(actual: T,  matcher: Matcher[T]) =
    this(null, true, fMatcher = matcher, fValue = actual.asInstanceOf[AnyRef])

  def this(message: String, expected: T, matcher: Matcher[T]) =
    this(message, true, fMatcher = matcher, fValue = expected.asInstanceOf[AnyRef])

  def this(message: String) =
    this(message, false, null, null)

  def this(assumption: String, t: Throwable) = {
    this(assumption, false, null, null)
    initCause(t)
  }
}
