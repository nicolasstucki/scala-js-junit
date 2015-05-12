package org.junit.internal;

import org.junit.Assert;

/*
 * Ported from https://github.com/junit-team/junit/blob/master/src/main/java/org/junit/ComparisonFailure.java
 */

object ArrayComparisonFailure {

  private[this] val serialVersionUID = 1L

}
/**
 * Thrown when two array elements differ
 *
 * @see Assert#assertArrayEquals(String, Object[], Object[])
 */
class ArrayComparisonFailure(
  fMessage: String
) extends AssertionError {

  /*
   * We have to use the f prefix until the next major release to ensure
   * serialization compatibility.
   * See https://github.com/junit-team/junit/issues/976
   */
  private var fIndices: List[Int] = Nil

  /**
   * Construct a new <code>ArrayComparisonFailure</code> with an error text and the array's
   * dimension that was not equal
   *
   * @param cause the exception that caused the array's content to fail the assertion test
   * @param index the array position of the objects that are not equal.
   * @see Assert#assertArrayEquals(String, Object[], Object[])
   */
  def this(message: String, cause: AssertionError, index: Int) {
    this(message)
    initCause(cause)
    addDimension(index)
  }

  def addDimension(index: Int) {
    fIndices = index :: fIndices
  }

  override def getMessage(): String = {
    val message = if (fMessage != null) fMessage else ""
    val indices = fIndices.map(index => s"[$index]").mkString
    val causeMessage = getCause().getMessage()
    s"${message}arrays first differed at element $indices; $causeMessage"
  }

  /**
   * {@inheritDoc}
   */
  override def toString(): String = getMessage()

}