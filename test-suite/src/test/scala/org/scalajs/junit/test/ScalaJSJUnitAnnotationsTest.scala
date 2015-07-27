package org.scalajs.junit.test

import org.junit._

object ScalaJSJUnitAnnotationsTest {
  @BeforeClass
  def beforeClassTest(): Unit = { }

  @AfterClass
  def afterClassTest(): Unit = { }
}

class ScalaJSJUnitAnnotationsTest {
  @Before
  def beforeTest(): Unit = { }

  @After
  def afterTest(): Unit = {  }

  @Test
  def test1(): Unit = { }

  @Test
  def test2(): Unit = { }

  @Test
  def test3(): Unit = { }

  @Ignore
  @Test
  def testIgnore(): Unit = { }

  @Ignore("This is the @Ignore message.")
  @Test
  def testIgnoreWithMessage(): Unit = { }
}
