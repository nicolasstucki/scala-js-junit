package org.hamcrest
/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */

import java.io.IOException
import java.lang.StringBuilder

/**
 * A {@link Description} that is stored as a string.
 */
object StringDescription {
  /**
   * Return the description of a {@link SelfDescribing} object as a String.
   *
   * @param selfDescribing
   *   The object to be described.
   * @return
   *   The description of the object.
   */
  def toString(selfDescribing: SelfDescribing): String = {
    new StringDescription().appendDescriptionOf(selfDescribing).toString()
  }

  /**
   * Alias for {@link #toString(SelfDescribing)}.
   */
  def asString(selfDescribing: SelfDescribing): String = {
      toString(selfDescribing)
  }
}

class StringDescription(
    out: Appendable = new StringBuilder()
  ) extends BaseDescription {

  override protected def append(str: String) {
    try {
      out.append(str)
    } catch {
      case e: IOException =>
        throw new RuntimeException("Could not write description", e);
    }
  }

  override protected def append(c: Char) {
    try {
      out.append(c)
    } catch {
      case e: IOException =>
        throw new RuntimeException("Could not write description", e);
    }
  }

  /**
   * Returns the description as a string.
   */
  override def toString(): String = out.toString()

}