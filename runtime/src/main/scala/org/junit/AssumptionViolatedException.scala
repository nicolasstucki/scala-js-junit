package org.junit

import org.hamcrest.Matcher

/**
 * An exception class used to implement <i>assumptions</i> (state in which a given test
 * is meaningful and should or should not be executed). A test for which an assumption
 * fails should not generate a test case failure.
 *
 * @see org.junit.Assume
 * @since 4.12
 */
// @SuppressWarnings("deprecation")
class AssumptionViolatedException[T] protected (
    fAssumption: String,
    fValueMatcher: Boolean,
    fMatcher: Matcher[_],
    fValue: AnyRef
  ) extends org.junit.internal.AssumptionViolatedException(fAssumption, fValueMatcher, fMatcher, fValue) {

  /**
   * An assumption exception with the given <i>actual</i> value and a <i>matcher</i> describing
   * the expectation that failed.
   */
  def this(actual: T,  matcher: Matcher[T]) {
    this(null, true, fMatcher = matcher, fValue = actual.asInstanceOf[AnyRef])
  }

  /**
   * An assumption exception with a message with the given <i>actual</i> value and a
   * <i>matcher</i> describing the expectation that failed.
   */
  def this(message: String, expected: T, matcher: Matcher[T]) {
    this(message, true, fMatcher = matcher, fValue = expected.asInstanceOf[AnyRef])
  }

  /**
   * An assumption exception with the given message only.
   */
  def this(message: String) {
    this(message, false, null, null)
  }

  /**
   * An assumption exception with the given message and a cause.
   */
  def this(assumption: String, t: Throwable) {
    this(assumption, false, null, null)
    initCause(t)
  }
}