package org.hamcrest.core

import org.hamcrest.Description
import org.hamcrest.DiagnosingMatcher
import org.hamcrest.Matcher


/**
 * Tests whether the value is an instance of a class.
 * Classes of basic types will be converted to the relevant "Object" classes
 */
class IsInstanceOf private (
      expectedClass: Class[_],
      matchableClass: Class[_]
  ) extends DiagnosingMatcher[AnyRef] {

  def this(expectedClass: Class[_]) {
    this(expectedClass, IsInstanceOf.matchableClass(expectedClass))
  }

  override protected def matches(item: AnyRef, mismatch: Description): Boolean = {
    if (null == item) {
      mismatch.appendText("null")
      false
    } else if (!matchableClass.isInstance(item)) {
      mismatch.appendValue(item).appendText(" is a " + item.getClass.getName)
      false
    } else true
  }

  override def describeTo(description: Description) {
    description.appendText("an instance of ").appendText(expectedClass.getName)
  }

}

object IsInstanceOf {

  private[IsInstanceOf] def matchableClass(expectedClass: Class[_]): Class[_] = {
    if (1.getClass == expectedClass) Int.box(1<<16).getClass
    else if (false.getClass == expectedClass) Boolean.box(false).getClass
    else if (0.toByte.getClass == expectedClass) Byte.box(0).getClass
    else if (' '.getClass == expectedClass) Char.box(' ').getClass
    else if (0d.getClass == expectedClass) Double.box(Double.MaxValue).getClass
    else if (0f.getClass == expectedClass) Float.box(Float.MaxValue).getClass
    else if (0L.getClass == expectedClass) Long.box(1<<32).getClass
    else if (0.toShort.getClass == expectedClass) Short.box(1<<8).getClass
    else expectedClass
  }

  /**
   * Creates a matcher that matches when the examined object is an instance of the specified <code>type</code>,
   * as determined by calling the {@link java.lang.Class#isInstance(Object)} method on that type, passing the
   * the examined object.
   *
   * <p>The created matcher assumes no relationship between specified type and the examined object.</p>
   * For example:
   * <pre>assertThat(new Canoe(), instanceOf(Paddlable.class));</pre>
   *
   */
//    @SuppressWarnings("unchecked")
  def instanceOf[T](typ: Class[_]): Matcher[T] = {
    new IsInstanceOf(typ).asInstanceOf[Matcher[T]]
  }

  /**
   * Creates a matcher that matches when the examined object is an instance of the specified <code>type</code>,
   * as determined by calling the {@link java.lang.Class#isInstance(Object)} method on that type, passing the
   * the examined object.
   *
   * <p>The created matcher forces a relationship between specified type and the examined object, and should be
   * used when it is necessary to make generics conform, for example in the JMock clause
   * <code>with(any(Thing.class))</code></p>
   * For example:
   * <pre>assertThat(new Canoe(), instanceOf(Canoe.class));</pre>
   *
   */
//    @SuppressWarnings("unchecked")
  def any[T](typ: Class[_]): Matcher[T] = {
     new IsInstanceOf(typ).asInstanceOf[Matcher[T]]
  }

}