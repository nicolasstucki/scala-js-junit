package org.junit.runner.manipulation

import org.junit.runner.Description

import scala.collection.JavaConversions._

abstract class Filter { self =>

  def shouldRun(description: Description): Boolean

  def describe(): String

  def apply(child: Object): Unit = {
    child match {
      case child: Filterable => child.filter(this)
    }
  }

  def intersect(second: Filter): Filter = {
    if (second == this || second == Filter.ALL) {
      this
    } else {
      new Filter() {
        override def shouldRun (description: Description): Boolean =
          self.shouldRun(description) && second.shouldRun(description)

        override def describe(): String =
          self.describe() + " and " + second.describe()
      }
    }
  }
}

object Filter {
  final val ALL = new Filter() {
    override def shouldRun(description: Description): Boolean = true

    override def describe(): String = "all tests"

    override def apply(child: AnyRef): Unit = {
      // do nothing
    }

    override def intersect(second: Filter): Filter = second
  }

  def matchMethodDescription(desiredDescription: Description): Filter = {
    new Filter() {
      override def shouldRun(description: Description): Boolean = {
        if (description.isTest) desiredDescription.equals(description)
        else description.getChildren.exists(shouldRun)
      }

      override def describe (): String =
        "Method " + desiredDescription.getDisplayName
    }
  }
}
