package org.scalajs.junit.test

import org.junit._
import org.junit.runners.MethodSorters

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class FixMethodOrderNameAscendingTest {
  @Test def test5(): Unit = { }

  @Test def test1(): Unit = { }

  @Test def test3(): Unit = { }

  @Test def test2(): Unit = { }

  @Test def test4(): Unit = { }
}

//@FixMethodOrder(MethodSorters.DEFAULT)
class FixMethodOrderDefaultTest {
  @Test def test5(): Unit = { }

  @Test def test1(): Unit = { }

  @Test def test3(): Unit = { }

  @Test def test2(): Unit = { }

  @Test def test4(): Unit = { }
}

//@FixMethodOrder(MethodSorters.JVM)
class FixMethodOrderJVMTest {
  @Test def test5(): Unit = { }

  @Test def test1(): Unit = { }

  @Test def test3(): Unit = { }

  @Test def test2(): Unit = { }

  @Test def test4(): Unit = { }
}
