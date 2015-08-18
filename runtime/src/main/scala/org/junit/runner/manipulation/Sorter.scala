package org.junit.runner.manipulation

import java.util.Comparator

import org.junit.runner.Description

class Sorter(comparator: Comparator[Description]) {
  def apply(obj: Object): Unit = {
    obj match {
      case obj: Sortable => obj.sort(this)
    }
  }

  def compare(o1: Description, o2: Description): Int =
    comparator.compare(o1, o2)
}

object Sorter {
  final val NULL: Sorter = new Sorter(new Comparator[Description]() {
    def compare(o1: Description, o2: Description): Int = 0
  })
}
