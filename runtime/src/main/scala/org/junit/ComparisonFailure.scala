package org.junit

/*
 * Ported from https://github.com/junit-team/junit/blob/master/src/main/java/org/junit/ComparisonFailure.java
 */

/**
 * Thrown when an {@link org.junit.Assert#assertEquals(Object, Object) assertEquals(String, String)} fails.
 * Create and throw a <code>ComparisonFailure</code> manually if you want to show users the
 * difference between two complex strings.
 * <p/>
 * Inspired by a patch from Alex Chaffee (alex@purpletech.com)
 *
 * @since 4.0
 */
object ComparisonFailure {
  /**
   * The maximum length for expected and actual strings. If it is exceeded, the strings should be shortened.
   *
   * @see ComparisonCompactor
   */
  private final def MAX_CONTEXT_LENGTH: Int = 20
  private final def serialVersionUID: Long = 1L

}

/**
 * Constructs a comparison failure.
 *
 * @param message the identifying message or null
 * @param expected the expected string value
 * @param actual the actual string value
 */
class ComparisonFailure(
    message: String,
     /*
     * We have to use the f prefix until the next major release to ensure
     * serialization compatibility.
     * See https://github.com/junit-team/junit/issues/976
     */
    fExpected: String,
    fActual: String
  ) extends AssertionError(message) {

  import ComparisonFailure._

  /**
   * Returns "..." in place of common prefix and "..." in place of common suffix between expected and actual.
   *
   * @see Throwable#getMessage()
   */
  override def getMessage(): String = {
    new ComparisonCompactor(MAX_CONTEXT_LENGTH, fExpected, fActual).compact(super.getMessage())
  }

  /**
   * Returns the actual string value
   *
   * @return the actual string value
   */
  def getActual(): String = fActual

  /**
   * Returns the expected string value
   *
   * @return the expected string value
   */
  def getExpected(): String = fExpected

  /**
   * @param contextLength the maximum length of context surrounding the difference between the compared strings.
   * When context length is exceeded, the prefixes and suffixes are compacted.
   * @param expected the expected string value
   * @param actual the actual string value
   */
  private class ComparisonCompactor(
    /**
     * The maximum length for <code>expected</code> and <code>actual</code> strings to show. When
     * <code>contextLength</code> is exceeded, the Strings are shortened.
     */
    private val contextLength: Int,
    private val expected: String,
    private val actual: String

    ) {

    private val ELLIPSIS: String = "..."
    private val DIFF_END: String = "]"
    private val DIFF_START: String = "["

    def compact(message: String): String = {
      if (expected == null || actual == null || expected.equals(actual)) {
        Assert.format(message, expected, actual)
      } else {
        val extractor = new DiffExtractor();
        val compactedPrefix = extractor.compactPrefix()
        val compactedSuffix = extractor.compactSuffix()
        Assert.format(message,
            compactedPrefix + extractor.expectedDiff() + compactedSuffix,
            compactedPrefix + extractor.actualDiff() + compactedSuffix)
      }
    }

    private[junit] def sharedPrefix(): String = {
      val end: Int = Math.min(expected.length(), actual.length());
      for (i <- 0 until end) {
        if (expected.charAt(i) != actual.charAt(i))
          return expected.substring(0, i)
      }
      expected.substring(0, end);
    }

    private def sharedSuffix(prefix: String): String = {
      var suffixLength = 0
      var maxSuffixLength = Math.min(expected.length() - prefix.length(),
          actual.length() - prefix.length()) - 1
      while(suffixLength <= maxSuffixLength) {
        if (expected.charAt(expected.length() - 1 - suffixLength)
            != actual.charAt(actual.length() - 1 - suffixLength)) {
          maxSuffixLength = suffixLength - 1 // break
        }
        suffixLength += 1
      }
      expected.substring(expected.length() - suffixLength)
    }

    /**
     * Can not be instantiated outside {@link org.junit.ComparisonFailure.ComparisonCompactor}.
     */
    private[ComparisonFailure] class DiffExtractor {

      private val _sharedPrefix: String = sharedPrefix()
      private val _sharedSuffix: String = sharedSuffix(sharedPrefix)

      def expectedDiff(): String = extractDiff(expected)

      def actualDiff(): String = extractDiff(actual)

      def compactPrefix(): String = {
          if (sharedPrefix.length() <= contextLength)
            sharedPrefix
          else
            ELLIPSIS + sharedPrefix.substring(sharedPrefix.length() - contextLength)
      }

      def compactSuffix(): String = {
          if (_sharedSuffix.length() <= contextLength)
            _sharedSuffix
          else
            _sharedSuffix.substring(0, contextLength) + ELLIPSIS
      }

      private def extractDiff(source: String): String = {
          return DIFF_START + source.substring(sharedPrefix.length(), source.length() - _sharedSuffix.length()) + DIFF_END
      }
    }
  }
}