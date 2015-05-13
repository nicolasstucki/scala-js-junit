package org.hamcrest

import org.hamcrest.Description.NullDescription

/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */

/**
 * A description of a Matcher. A Matcher will describe itself to a description
 * which can later be used for reporting.
 *
 * @see Matcher#describeTo(Description)
 */
object Description {
  /**
   * A description that consumes input but does nothing.
   */
  val NONE: Description = new NullDescription

  final class NullDescription extends Description {

    override def appendDescriptionOf(value: SelfDescribing): Description = this

    override def appendList(start: String, separator: String, end: String,
        values: java.lang.Iterable[SelfDescribing]): Description = {
      this
    }

    override def appendText(text: String): Description = this

    override def appendValue(value: AnyRef): Description = this

    override def appendValueList[T](start: String, separator: String,
        end: String, values: T*): Description = {
      this
    }

    override def appendValueList[T](start: String, separator: String,
        end: String, values: java.lang.Iterable[T]): Description = {
      this
    }

    override def toString(): String = ""
  }

}

trait Description {

  /**
   * Appends some plain text to the description.
   */
  def appendText(text: String): Description

  /**
   * Appends the description of a {@link SelfDescribing} value to this description.
   */
  def appendDescriptionOf(value: SelfDescribing): Description

  /**
   * Appends an arbitrary value to the description.
   */
  def appendValue(value: AnyRef): Description

  /**
   * Appends a list of values to the description.
   */
  def appendValueList[T](start: String, separator: String, end: String, values: T*): Description

  /**
   * Appends a list of values to the description.
   */
  def appendValueList[T](start: String, separator: String, end: String, values: java.lang.Iterable[T]): Description

  /**
   * Appends a list of {@link org.hamcrest.SelfDescribing} objects
   * to the description.
   */
  def appendList(start: String, separator: String, end: String, values: java.lang.Iterable[SelfDescribing]): Description
}