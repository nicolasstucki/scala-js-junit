package org.junit.runner.manipulation

trait Filterable {
  def filter(filter: Filter): Unit
}
