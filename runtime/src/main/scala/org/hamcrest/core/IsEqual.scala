package org.hamcrest.core

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

import java.lang.reflect.Array


/**
 * Is the value equal to another value, as tested by the
 * {@link java.lang.Object#equals} invokedMethod?
 */
class IsEqual[T](
    expectedValue: AnyRef
  ) extends BaseMatcher[T] {

  import IsEqual._

  override def matches(actualValue: AnyRef): Boolean =
    areEqual(actualValue, expectedValue)

  override def describeTo(description: Description) {
      description.appendValue(expectedValue)
  }

}

object IsEqual {

    private[IsEqual] def areEqual(actual: AnyRef, expected: AnyRef): Boolean = {
      if (actual == null)
        expected == null
      else if (expected != null && isArray(actual))
        isArray(expected) && areArraysEqual(actual, expected)
      else
        actual.equals(expected)
    }

    private[IsEqual] def areArraysEqual(actualArray: AnyRef, expectedArray: AnyRef): Boolean =
      areArrayLengthsEqual(actualArray, expectedArray) && areArrayElementsEqual(actualArray, expectedArray)

    private[IsEqual] def areArrayLengthsEqual(actualArray: AnyRef, expectedArray: AnyRef): Boolean =
      Array.getLength(actualArray) == Array.getLength(expectedArray)


    private[IsEqual] def areArrayElementsEqual(actualArray: AnyRef, expectedArray: AnyRef): Boolean ={
        for (i <- 0 until Array.getLength(actualArray))
          if (!areEqual(Array.get(actualArray, i), Array.get(expectedArray, i)))
            return false
        true
    }

    private[IsEqual] def isArray(o: AnyRef): Boolean =
      o.getClass().isArray()

    /**
     * Creates a matcher that matches when the examined object is logically equal to the specified
     * <code>operand</code>, as determined by calling the {@link java.lang.Object#equals} method on
     * the <b>examined</b> object.
     *
     * <p>If the specified operand is <code>null</code> then the created matcher will only match if
     * the examined object's <code>equals</code> method returns <code>true</code> when passed a
     * <code>null</code> (which would be a violation of the <code>equals</code> contract), unless the
     * examined object itself is <code>null</code>, in which case the matcher will return a positive
     * match.</p>
     *
     * <p>The created matcher provides a special behaviour when examining <code>Array</code>s, whereby
     * it will match if both the operand and the examined object are arrays of the same length and
     * contain items that are equal to each other (according to the above rules) <b>in the same
     * indexes</b>.</p>
     * For example:
     * <pre>
     * assertThat("foo", equalTo("foo"));
     * assertThat(new String[] {"foo", "bar"}, equalTo(new String[] {"foo", "bar"}));
     * </pre>
     *
     */
    def equalTo[T](operand: T): Matcher[T] =
      new IsEqual[T](operand.asInstanceOf[AnyRef])

    /**
     * Creates an {@link org.hamcrest.core.IsEqual} matcher that does not enforce the values being
     * compared to be of the same static type.
     */
    def equalToObject(operand: AnyRef): Matcher[AnyRef] =
      new IsEqual[AnyRef](operand)

}