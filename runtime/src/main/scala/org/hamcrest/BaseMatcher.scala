package org.hamcrest;

/**
 * BaseClass for all Matcher implementations.
 *
 * @see Matcher
 */
abstract class BaseMatcher[T] extends Matcher[T] {

  /**
   * @see Matcher#_dont_implement_Matcher___instead_extend_BaseMatcher_()
   */

//  @deprecated
//  override final def _dont_implement_Matcher___instead_extend_BaseMatcher_() {
//      // See Matcher interface for an explanation of this method.
//  }

  override def describeMismatch(item: AnyRef, description: Description) {
      description.appendText("was ").appendValue(item)
  }

  override def toString() = StringDescription.toString(this)

}