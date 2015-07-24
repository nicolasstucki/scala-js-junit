package org.scalajs.junit.test

import org.junit._

class TraitTest extends TestTrait1 with TestTrait2

trait TestTrait1 {
  @Test def test1: Unit = {}
  @Test def test2: Unit = {}
}

trait TestTrait2 {
  @Test def test3: Unit = {}
  @Test def test4: Unit = {}
}