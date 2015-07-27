/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit

@SerialVersionUID(1L)
object ComparisonFailure {
  private final def MAX_CONTEXT_LENGTH: Int = 20
}

class ComparisonFailure(message: String, fExpected: String, fActual: String)
    extends AssertionError(message) {

  import ComparisonFailure._

  override def getMessage(): String = {
    val cc = new ComparisonCompactor(MAX_CONTEXT_LENGTH, fExpected, fActual)
    cc.compact(super.getMessage)
  }

  def getActual(): String = fActual

  def getExpected(): String = fExpected

  private class ComparisonCompactor(private val contextLength: Int,
      private val expected: String, private val actual: String) {

    private val ELLIPSIS: String = "..."
    private val DIFF_END: String = "]"
    private val DIFF_START: String = "["

    def compact(message: String): String = {
      if (expected == null || actual == null || expected.equals(actual)) {
        Assert.format(message, expected, actual)
      } else {
        val extractor = new DiffExtractor()
        val compactedPrefix = extractor.compactPrefix()
        val compactedSuffix = extractor.compactSuffix()
        Assert.format(message,
            compactedPrefix + extractor.expectedDiff() + compactedSuffix,
            compactedPrefix + extractor.actualDiff() + compactedSuffix)
      }
    }

    private[junit] def sharedPrefix(): String = {
      val end: Int = Math.min(expected.length, actual.length)
      (0 until end).find(i => expected.charAt(i) != actual.charAt(i))
        .fold(expected.substring(0, end))(expected.substring(0, _))
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
        val sub = source.substring(sharedPrefix.length(),
            source.length() - _sharedSuffix.length())
        DIFF_START + sub + DIFF_END
      }
    }
  }
}
