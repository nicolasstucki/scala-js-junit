package org.scalajs.junit.test

import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit.Assume._
import org.junit._
import org.junit.internal.AssumptionViolatedException

class ScalaJSJUnitAssumptionsTest {

  private val shallNotPass = false

  def testIfAssumePass(assumption: =>Unit, shouldPass: Boolean = true) {
    try {
      assumption
      if(!shouldPass)
        fail("Assumption should have failed")
    } catch {
      case assVio: AssumptionViolatedException =>
        if(shouldPass)
          throw assVio
    }
  }

  @Test
  def testAssumeTrue() {
    testIfAssumePass(assumeTrue("true be assumed to be true", true))
    testIfAssumePass(assumeTrue(true))
    testIfAssumePass(assumeTrue("false be assumed to be true", false), shallNotPass)
    testIfAssumePass(assumeTrue( false), shallNotPass)

    testIfAssumePass(assumeFalse("false be assumed to be false", false))
    testIfAssumePass(assumeFalse(false))
    testIfAssumePass(assumeFalse("true be assumed to be false", true), shallNotPass)
    testIfAssumePass(assumeFalse(true), shallNotPass)
  }

  @Test
  def testAssumeNotNull() {
    testIfAssumePass(assumeNotNull())
    testIfAssumePass(assumeNotNull(new Object))
    testIfAssumePass(assumeNotNull("", new Object, " "))

    testIfAssumePass(assumeNotNull(null), shallNotPass)
    testIfAssumePass(assumeNotNull(new Object, null), shallNotPass)
    testIfAssumePass(assumeNotNull(null, new Object), shallNotPass)
  }

  @Test
  def testAssumeThat() {
    testIfAssumePass(assumeThat(null, nullValue()))
    testIfAssumePass(assumeThat(null, notNullValue()), shallNotPass)

    testIfAssumePass(assumeThat(new Object, notNullValue()))
    testIfAssumePass(assumeThat(new Object, nullValue()), shallNotPass)

    testIfAssumePass(assumeThat(new Object, notNullValue(classOf[AnyRef])))

    testIfAssumePass(assumeThat(1, is(1)))
    testIfAssumePass(assumeThat(1, is(2)), shallNotPass)

    testIfAssumePass(assumeThat(1, not(is(2))))
    testIfAssumePass(assumeThat(1, not(is(1))), shallNotPass)

    testIfAssumePass(assumeThat(1, is(not(2))))
    testIfAssumePass(assumeThat(1, is(not(1))), shallNotPass)

    testIfAssumePass(assumeThat(1, not(2)))
    testIfAssumePass(assumeThat(1, not(1)), shallNotPass)
  }

  @Test
  def testAssumesNoException(): Unit = {
    testIfAssumePass(assumeNoException("assumeNoException(null) should succeed", null))
    testIfAssumePass(assumeNoException(null))

    testIfAssumePass(assumeNoException("assumeNoException(new Throwable) should succeed", new Throwable), shallNotPass)
    testIfAssumePass(assumeNoException(new Throwable), shallNotPass)
  }

}
