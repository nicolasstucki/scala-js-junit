package org.junit.runners


object MethodSorters {
  object NAME_ASCENDING extends MethodSorters {
    val comparator: Ordering[String] = {
      new Ordering[String] {
        def compare (x: String, y: String): Int = x.compareTo(y)
      }
    }
  }

  object JVM extends MethodSorters {
    val comparator: Ordering[String] = {
      new Ordering[String] {
        def compare (x: String, y: String): Int = 0
      }
    }
  }

  object DEFAULT extends MethodSorters {
    val comparator: Ordering[String] = {
      new Ordering[String] {
        def compare (x: String, y: String): Int = 0
      }
    }
  }
}

trait MethodSorters {
  def comparator: Ordering[String]
}
