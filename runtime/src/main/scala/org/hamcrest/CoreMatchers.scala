package org.hamcrest

import org.hamcrest.core._

/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */

// @SuppressWarnings("UnusedDeclaration")
object CoreMatchers {

//  /**
//   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
//   * For example:
//   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
//   */
//  def allOf[T](matchers: java.lang.Iterable[Matcher[T]]): Matcher[T] = AllOf.allOf(matchers)
//
//  /**
//   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
//   * For example:
//   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
//   */
//  // @SafeVarargs
//  def allOf[T](matchers: Matcher[T]*): Matcher[T] = AllOf.allOf(matchers)
//
//  /**
//   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
//   * For example:
//   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
//   */
//  def anyOf[T](matchers: java.lang.Iterable[Matcher[T]]): AnyOf[T] = AnyOf.anyOf(matchers)
//
//  /**
//   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
//   * For example:
//   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
//   */
//  // @SafeVarargs
//  def anyOf[T](matchers: Matcher[T]*) : AnyOf = AnyOf.anyOf(matchers)
//
//  /**
//   * Creates a matcher that matches when both of the specified matchers match the examined object.
//   * For example:
//   * <pre>assertThat("fab", both(containsString("a")).and(containsString("b")))</pre>
//   */
//  def both[LHS](matcher: Matcher[LHS]): CombinableMatcher.CombinableBothMatcher[LHS] = CombinableMatcher.both(matcher)
//
//  /**
//   * Creates a matcher that matches when either of the specified matchers match the examined object.
//   * For example:
//   * <pre>assertThat("fan", either(containsString("a")).or(containsString("b")))</pre>
//   */
//  def either[LHS](matcher: Matcher[LHS]): CombinableMatcher.CombinableEitherMatcher[LHS] = CombinableMatcher.either(matcher)
//
//  /**
//   * Wraps an existing matcher, overriding its description with that specified.  All other functions are
//   * delegated to the decorated matcher, including its mismatch description.
//   * For example:
//   * <pre>describedAs("a big decimal equal to %0", equalTo(myBigDecimal), myBigDecimal.toPlainString())</pre>
//   *
//   * @param description
//   *     the new description for the wrapped matcher
//   * @param matcher
//   *     the matcher to wrap
//   * @param values
//   *     optional values to insert into the tokenised description
//   */
//  def describedAs[T](description: String, matcher: Matcher[T], values: AnyRef*): Matcher[T] = DescribedAs.describedAs(description, matcher, values)

//  /**
//   * Creates a matcher for {@link Iterable}s that only matches when a single pass over the
//   * examined {@link Iterable} yields items that are all matched by the specified
//   * <code>itemMatcher</code>.
//   * For example:
//   * <pre>assertThat(Arrays.asList("bar", "baz"), everyItem(startsWith("ba")))</pre>
//   *
//   * @param itemMatcher
//   *     the matcher to apply to every item provided by the examined {@link Iterable}
//   */
//  def everyItem[U](itemMatcher: Matcher[U]): Matcher[java.lang.Iterable[U]] = Every.everyItem(itemMatcher)

  /**
   * Decorates another Matcher, retaining its behaviour, but allowing tests
   * to be slightly more expressive.
   * For example:
   * <pre>assertThat(cheese, is(equalTo(smelly)))</pre>
   * instead of:
   * <pre>assertThat(cheese, equalTo(smelly))</pre>
   */
  def is[T](matcher: Matcher[T]): Matcher[T]  = Is.is(matcher)

  /**
   * A shortcut to the frequently used <code>is(equalTo(x))</code>.
   * For example:
   * <pre>assertThat(cheese, is(smelly))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(equalTo(smelly)))</pre>
   */
  def is[T](value: T): Matcher[T] = Is.is(value)

  /**
   * A shortcut to the frequently used <code>is(instanceOf(SomeClass.class))</code>.
   * For example:
   * <pre>assertThat(cheese, isA(Cheddar.class))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(instanceOf(Cheddar.class)))</pre>
   */
  def isA[T](typ: java.lang.Class[T]): Matcher[T] = Is.isA(typ)
//
//  /**
//   * Creates a matcher that always matches, regardless of the examined object.
//   */
//  def anything(): Matcher[AnyRef] = IsAnything.anything()
//
//  /**
//   * Creates a matcher that always matches, regardless of the examined object, but describes
//   * itself with the specified {@link String}.
//   *
//   * @param description
//   *     a meaningful {@link String} used when describing itself
//   */
//  def anything(description: String): Matcher[AnyRef] = IsAnything.anything(description)
//
//  /**
//   * Creates a matcher for {@link Iterable}s that only matches when a single pass over the
//   * examined {@link Iterable} yields at least one item that is matched by the specified
//   * <code>itemMatcher</code>.  Whilst matching, the traversal of the examined {@link Iterable}
//   * will stop as soon as a matching item is found.
//   * For example:
//   * <pre>assertThat(Arrays.asList("foo", "bar"), hasItem(startsWith("ba")))</pre>
//   *
//   * @param itemMatcher
//   *     the matcher to apply to items provided by the examined {@link Iterable}
//   */
//  def hasItem[T](itemMatcher: Matcher[T]): Match[Iterable[T]] = IsCollectionContaining.hasItem(itemMatcher)
//
//  /**
//   * Creates a matcher for {@link Iterable}s that only matches when a single pass over the
//   * examined {@link Iterable} yields at least one item that is equal to the specified
//   * <code>item</code>.  Whilst matching, the traversal of the examined {@link Iterable}
//   * will stop as soon as a matching item is found.
//   * For example:
//   * <pre>assertThat(Arrays.asList("foo", "bar"), hasItem("bar"))</pre>
//   *
//   * @param item
//   *     the item to compare against the items provided by the examined {@link Iterable}
//   */
//  def hasItem[T](item: T): Matcher[Iterable[T]] = IsCollectionContaining.hasItem(item)
//
//  /**
//   * Creates a matcher for {@link Iterable}s that matches when consecutive passes over the
//   * examined {@link Iterable} yield at least one item that is matched by the corresponding
//   * matcher from the specified <code>itemMatchers</code>.  Whilst matching, each traversal of
//   * the examined {@link Iterable} will stop as soon as a matching item is found.
//   * For example:
//   * <pre>assertThat(Arrays.asList("foo", "bar", "baz"), hasItems(endsWith("z"), endsWith("o")))</pre>
//   *
//   * @param itemMatchers
//   *     the matchers to apply to items provided by the examined {@link Iterable}
//   */
//  // @SafeVarargs
//  def hasItems[T](itemMatchers:Matcher[T]*): Matcher[T] = IsCollectionContaining.hasItems(itemMatchers)

//  /**
//   * Creates a matcher for {@link Iterable}s that matches when consecutive passes over the
//   * examined {@link Iterable} yield at least one item that is equal to the corresponding
//   * item from the specified <code>items</code>.  Whilst matching, each traversal of the
//   * examined {@link Iterable} will stop as soon as a matching item is found.
//   * For example:
//   * <pre>assertThat(Arrays.asList("foo", "bar", "baz"), hasItems("baz", "foo"))</pre>
//   *
//   * @param items
//   *     the items to compare against the items provided by the examined {@link Iterable}
//   */
//  // @SafeVarargs
//  public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItems(T... items) {
//    return org.hamcrest.core.IsCollectionContaining.hasItems(items);
//  }
//
//  /**
//   * Creates a matcher that matches when the examined object is logically equal to the specified
//   * <code>operand</code>, as determined by calling the {@link java.lang.Object#equals} method on
//   * the <b>examined</b> object.
//   *
//   * <p>If the specified operand is <code>null</code> then the created matcher will only match if
//   * the examined object's <code>equals</code> method returns <code>true</code> when passed a
//   * <code>null</code> (which would be a violation of the <code>equals</code> contract), unless the
//   * examined object itself is <code>null</code>, in which case the matcher will return a positive
//   * match.</p>
//   *
//   * <p>The created matcher provides a special behaviour when examining <code>Array</code>s, whereby
//   * it will match if both the operand and the examined object are arrays of the same length and
//   * contain items that are equal to each other (according to the above rules) <b>in the same
//   * indexes</b>.</p>
//   * For example:
//   * <pre>
//   * assertThat("foo", equalTo("foo"));
//   * assertThat(new String[] {"foo", "bar"}, equalTo(new String[] {"foo", "bar"}));
//   * </pre>
//   */
//  public static <T> org.hamcrest.Matcher<T> equalTo(T operand) {
//    return org.hamcrest.core.IsEqual.equalTo(operand);
//  }
//
//  /**
//   * Creates an {@link org.hamcrest.core.IsEqual} matcher that does not enforce the values being
//   * compared to be of the same static type.
//   */
//  public static org.hamcrest.Matcher<java.lang.Object> equalToObject(java.lang.Object operand) {
//    return org.hamcrest.core.IsEqual.equalToObject(operand);
//  }

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
   */
  def any[T](typ: Class[T]): Matcher[T] =
    core.IsInstanceOf.any(typ)

  /**
   * Creates a matcher that matches when the examined object is an instance of the specified <code>type</code>,
   * as determined by calling the {@link java.lang.Class#isInstance(Object)} method on that type, passing the
   * the examined object.
   *
   * <p>The created matcher assumes no relationship between specified type and the examined object.</p>
   * For example:
   * <pre>assertThat(new Canoe(), instanceOf(Paddlable.class));</pre>
   */
  def instanceOf[T](typ: Class[_]): Matcher[T] =
    core.IsInstanceOf.instanceOf(typ)

  /**
   * Creates a matcher that wraps an existing matcher, but inverts the logic by which
   * it will match.
   * For example:
   * <pre>assertThat(cheese, is(not(equalTo(smelly))))</pre>
   *
   * @param matcher
   *     the matcher whose sense should be inverted
   */
  def not[T](matcher: Matcher[T]): Matcher[T] =
    core.IsNot.not(matcher)

  /**
   * A shortcut to the frequently used <code>not(equalTo(x))</code>.
   * For example:
   * <pre>assertThat(cheese, is(not(smelly)))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(equalTo(smelly))))</pre>
   *
   * @param value
   *     the value that any examined object should <b>not</b> equal
   */
  def not[T](value: T): Matcher[T] =
    core.IsNot.not(value)


  /**
   * A shortcut to the frequently used <code>not(nullValue())</code>.
   * For example:
   * <pre>assertThat(cheese, is(notNullValue()))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(nullValue())))</pre>
   */
  def notNullValue(): Matcher[AnyRef] =
    core.IsNull.notNullValue()

  /**
   * A shortcut to the frequently used <code>not(nullValue(X.class)). Accepts a
   * single dummy argument to facilitate type inference.</code>.
   * For example:
   * <pre>assertThat(cheese, is(notNullValue(X.class)))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(nullValue(X.class))))</pre>
   *
   * @param type
   *     dummy parameter used to infer the generic type of the returned matcher
   */
  def notNullValue[T<:AnyRef](typ: java.lang.Class[T]): Matcher[T] =
    core.IsNull.notNullValue(typ)


  /**
   * Creates a matcher that matches if examined object is <code>null</code>.
   * For example:
   * <pre>assertThat(cheese, is(nullValue())</pre>
   */
  def nullValue(): Matcher[AnyRef] =
     core.IsNull.nullValue()

  /**
   * Creates a matcher that matches if examined object is <code>null</code>. Accepts a
   * single dummy argument to facilitate type inference.
   * For example:
   * <pre>assertThat(cheese, is(nullValue(Cheese.class))</pre>
   *
   * @param type
   *     dummy parameter used to infer the generic type of the returned matcher
   */
  def nullValue[T<:AnyRef](typ: java.lang.Class[T]): Matcher[T] =
    core.IsNull.nullValue(typ)

//
//  /**
//   * Creates a matcher that matches only when the examined object is the same instance as
//   * the specified target object.
//   *
//   * @param target
//   *     the target instance against which others should be assessed
//   */
//  public static <T> org.hamcrest.Matcher<T> sameInstance(T target) {
////    return org.hamcrest.core.IsSame.sameInstance(target);
////  }
//
//  /**
//   * Creates a matcher that matches only when the examined object is the same instance as
//   * the specified target object.
//   *
//   * @param target
//   *     the target instance against which others should be assessed
//   */
//  public static <T> org.hamcrest.Matcher<T> theInstance(T target) {
////    return org.hamcrest.core.IsSame.theInstance(target);
////  }
//
//  /**
//   * Creates a matcher that matches if the examined {@link String} contains the specified
//   * {@link String} anywhere.
//   * For example:
//   * <pre>assertThat("myStringOfNote", containsString("ring"))</pre>
//   *
//   * @param substring
//   *     the substring that the returned matcher will expect to find within any examined string
//   */
//  public static org.hamcrest.Matcher<java.lang.String> containsString(java.lang.String substring) {
////    return org.hamcrest.core.StringContains.containsString(substring);
////  }
//
//  /**
//   * Creates a matcher that matches if the examined {@link String} contains the specified
//   * {@link String} anywhere, ignoring case.
//   * For example:
//   * <pre>assertThat("myStringOfNote", containsString("ring"))</pre>
//   *
//   * @param substring
//   *     the substring that the returned matcher will expect to find within any examined string
//   */
//  public static org.hamcrest.Matcher<java.lang.String> containsStringIgnoringCase(java.lang.String substring) {
////    return org.hamcrest.core.StringContains.containsStringIgnoringCase(substring);
////  }
//
//  /**
//   * <p>
//   * Creates a matcher that matches if the examined {@link String} starts with the specified
//   * {@link String}.
//   * </p>
//   * For example:
//   * <pre>assertThat("myStringOfNote", startsWith("my"))</pre>
//   *
//   * @param prefix
//   *      the substring that the returned matcher will expect at the start of any examined string
//   */
//  def startsWith(prefix: String): Matcher[String] =
//    core.StringStartsWith.startsWith(prefix)
//
//  /**
//   * <p>
//   * Creates a matcher that matches if the examined {@link String} starts with the specified
//   * {@link String}, ignoring case
//   * </p>
//   * For example:
//   * <pre>assertThat("myStringOfNote", startsWith("my"))</pre>
//   *
//   * @param prefix
//   *      the substring that the returned matcher will expect at the start of any examined string
//   */
//  def startsWithIgnoringCase(prefix: String): Matcher[String] =
//    core.StringStartsWith.startsWithIgnoringCase(prefix)
//
//  /**
//   * Creates a matcher that matches if the examined {@link String} ends with the specified
//   * {@link String}.
//   * For example:
//   * <pre>assertThat("myStringOfNote", endsWith("Note"))</pre>
//   *
//   * @param suffix
//   *      the substring that the returned matcher will expect at the end of any examined string
//   */
//  def endsWith(suffix: String): Matcher[String] =
//    core.StringEndsWith.endsWith(suffix)
//
//  /**
//   * Creates a matcher that matches if the examined {@link String} ends with the specified
//   * {@link String}, ignoring case.
//   * For example:
//   * <pre>assertThat("myStringOfNote", endsWith("Note"))</pre>
//   *
//   * @param suffix
//   *      the substring that the returned matcher will expect at the end of any examined string
//   */
//  def endsWithIgnoringCase(suffix: String): Matcher[String] =
//    core.StringEndsWith.endsWithIgnoringCase(suffix)

}