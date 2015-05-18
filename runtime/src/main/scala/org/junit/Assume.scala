package org.junit;

import java.util.Arrays.asList
//import org.hamcrest.CoreMatchers.everyItem
import org.hamcrest.CoreMatchers.is
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.Matcher
import java.util.Arrays.ArrayList

/**
 * A set of methods useful for stating assumptions about the conditions in which a test is meaningful.
 * A failed assumption does not mean the code is broken, but that the test provides no useful information. Assume
 * basically means "don't run this test if these conditions don't apply". The default JUnit runner skips tests with
 * failing assumptions. Custom runners may behave differently.
 * <p>
 *     A good example of using assumptions is in <a href="https://github.com/junit-team/junit/wiki/Theories">Theories</a> where they are needed to exclude certain datapoints that aren't suitable or allowed for a certain test case.
 * </p>
 * Failed assumptions are usually not logged, because there may be many tests that don't apply to certain
 * configurations.
 *
 * <p>
 * These methods can be used directly: <code>Assume.assumeTrue(...)</code>, however, they
 * read better if they are referenced through static import:<br/>
 * <pre>
 * import static org.junit.Assume.*;
 *    ...
 *    assumeTrue(...);
 * </pre>
 * </p>
 *
 * @see <a href="https://github.com/junit-team/junit/wiki/Theories">Theories</a>
 *
 * @since 4.4
 */
object Assume {

  /**
   * If called with an expression evaluating to {@code false}, the test will halt and be ignored.
   */
  def assumeTrue(b: Boolean) {
    assumeThat(b, is(true))
  }

  /**
   * The inverse of {@link #assumeTrue(boolean)}.
   */
  def assumeFalse(b: Boolean) {
    assumeTrue(!b)
  }

  /**
   * If called with an expression evaluating to {@code false}, the test will halt and be ignored.
   *
   * @param b If <code>false</code>, the method will attempt to stop the test and ignore it by
   * throwing {@link AssumptionViolatedException}.
   * @param message A message to pass to {@link AssumptionViolatedException}.
   */
  def assumeTrue(message: String, b: Boolean) {
    if (!b)
      throw new AssumptionViolatedException(message)
  }

  /**
   * The inverse of {@link #assumeTrue(String, boolean)}.
   */
  def assumeFalse(message: String, b: Boolean) {
    assumeTrue(message, !b)
  }

  /**
   * If called with one or more null elements in <code>objects</code>, the test will halt and be ignored.
   */
  def assumeNotNull(objects: AnyRef*) {
    // assumeThat(objects.toList, everyItem(notNullValue()))
    // TODO: check if semantic of the operation is equivalent
    objects foreach (assumeThat(_, notNullValue()))
  }

  /**
   * Call to assume that <code>actual</code> satisfies the condition specified by <code>matcher</code>.
   * If not, the test halts and is ignored.
   * Example:
   * <pre>:
   *   assumeThat(1, is(1)); // passes
   *   foo(); // will execute
   *   assumeThat(0, is(1)); // assumption failure! test halts
   *   int x = 1 / 0; // will never execute
   * </pre>
   *
   * @param <T> the static type accepted by the matcher (this can flag obvious compile-time problems such as {@code assumeThat(1, is("a"))}
   * @param actual the computed value being compared
   * @param matcher an expression, built of {@link Matcher}s, specifying allowed values
   * @see org.hamcrest.CoreMatchers
   * @see org.junit.matchers.JUnitMatchers
   */
  def assumeThat[T](actual: T, matcher: Matcher[T]) {
    if (!matcher.matches(actual.asInstanceOf[AnyRef])) {
      throw new AssumptionViolatedException(actual, matcher)
    }
  }

  /**
   * Call to assume that <code>actual</code> satisfies the condition specified by <code>matcher</code>.
   * If not, the test halts and is ignored.
   * Example:
   * <pre>:
   *   assumeThat("alwaysPasses", 1, is(1)); // passes
   *   foo(); // will execute
   *   assumeThat("alwaysFails", 0, is(1)); // assumption failure! test halts
   *   int x = 1 / 0; // will never execute
   * </pre>
   *
   * @param <T> the static type accepted by the matcher (this can flag obvious compile-time problems such as {@code assumeThat(1, is("a"))}
   * @param actual the computed value being compared
   * @param matcher an expression, built of {@link Matcher}s, specifying allowed values
   * @see org.hamcrest.CoreMatchers
   * @see org.junit.matchers.JUnitMatchers
   */
  def assumeThat[T](message: String, actual: T, matcher: Matcher[T]) {
    if (!matcher.matches(actual.asInstanceOf[AnyRef])) {
      throw new AssumptionViolatedException(message, actual, matcher)
    }
  }

  /**
   * Use to assume that an operation completes normally.  If {@code e} is non-null, the test will halt and be ignored.
   *
   * For example:
   * <pre>
   * \@Test public void parseDataFile() {
   *   DataFile file;
   *   try {
   *     file = DataFile.open("sampledata.txt");
   *   } catch (IOException e) {
   *     // stop test and ignore if data can't be opened
   *     assumeNoException(e);
   *   }
   *   // ...
   * }
   * </pre>
   *
   * @param e if non-null, the offending exception
   */
  def assumeNoException(e: Throwable) {
    assumeThat(e, nullValue())
  }

  /**
   * Attempts to halt the test and ignore it if Throwable <code>e</code> is
   * not <code>null</code>. Similar to {@link #assumeNoException(Throwable)},
   * but provides an additional message that can explain the details
   * concerning the assumption.
   *
   * @param e if non-null, the offending exception
   * @param message Additional message to pass to {@link AssumptionViolatedException}.
   * @see #assumeNoException(Throwable)
   */
  def assumeNoException(message: String, e: Throwable) {
    assumeThat(message, e, nullValue())
  }
}