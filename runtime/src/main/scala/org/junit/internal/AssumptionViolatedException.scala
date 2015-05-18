package org.junit.internal;

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.SelfDescribing
import org.hamcrest.StringDescription

/**
 * An exception class used to implement <i>assumptions</i> (state in which a given test
 * is meaningful and should or should not be executed). A test for which an assumption
 * fails should not generate a test case failure.
 *
 * @see org.junit.Assume
 */
class AssumptionViolatedException protected(
    fAssumption: String,
    fValueMatcher: Boolean,
    fMatcher: Matcher[_],
    fValue: AnyRef
  ) extends RuntimeException with SelfDescribing {

//  /**
//   * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
//   */
//  @deprecated
//  def this(assumption: String, hasValue: Boolean, value: AnyRef, matcher: Matcher[AnyRef]) {
//    this(assumption, hasValue, matcher, value)
//    if (value.isInstanceOf[Throwable]) {
//      initCause(value.asInstanceOf[Throwable])
//    }
//  }
//
//  /**
//   * An assumption exception with the given <i>value</i> (String or
//   * Throwable) and an additional failing {@link Matcher}.
//   *
//   * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
//   */
//  @deprecated
//  def this(value: AnyRef, matcher: Matcher[AnyRef]) {
//	  this(null, true, matcher, value)
//  }
//
//  /**
//   * An assumption exception with the given <i>value</i> (String or
//   * Throwable) and an additional failing {@link Matcher}.
//   *
//   * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
//   */
//  @deprecated
//  def this(assumption: String, value: AnyRef, matcher: Matcher[AnyRef]) {
//    this(assumption, true, matcher, value)
//  }
//
//  /**
//   * An assumption exception with the given message only.
//   *
//   * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
//   */
//  @deprecated
//  def this(assumption: String) {
//    this(assumption, false, fMatcher = null, null)
//  }
//
//  /**
//   * An assumption exception with the given message and a cause.
//   *
//   * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
//   */
//  @deprecated
//  def this(assumption: String, e: Throwable) {
//    this(assumption, false, fMatcher = null, null)
//    initCause(e)
//  }

  override def getMessage() = {
    StringDescription.asString(this)
  }

  def describeTo(description: Description) {
    if (fAssumption != null) {
      description.appendText(fAssumption);
    }

    if (fValueMatcher) {
      // a value was passed in when this instance was constructed; print it
      if (fAssumption != null) {
        description.appendText(": ")
      }

      description.appendText("got: ")
      description.appendValue(fValue)

      if (fMatcher != null) {
        description.appendText(", expected: ")
        description.appendDescriptionOf(fMatcher)
      }
    }
  }
}