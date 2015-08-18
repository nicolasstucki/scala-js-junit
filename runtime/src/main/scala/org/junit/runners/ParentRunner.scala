package org.junit.runners

import org.junit.runner.Runner
import org.junit.runner.manipulation.Filterable
import org.junit.runner.manipulation.Sortable

abstract class ParentRunner[T](testClass: Class[_]) extends Runner with Filterable with Sortable {
  // Dummy for classOf[...]
}
