package org.hamcrest


abstract class DiagnosingMatcher[T<:AnyRef] extends BaseMatcher[T] {

  override final def matches(item: AnyRef): Boolean = matches(item, Description.NONE)

  override final def describeMismatch(item: AnyRef, mismatchDescription: Description) {
    matches(item, mismatchDescription)
  }

  protected def matches(item: AnyRef, mismatchDescription: Description): Boolean

}