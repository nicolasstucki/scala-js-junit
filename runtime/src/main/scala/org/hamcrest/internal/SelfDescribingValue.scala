package org.hamcrest.internal

import org.hamcrest.Description
import org.hamcrest.SelfDescribing

class SelfDescribingValue[T](
    value: T
  ) extends SelfDescribing {

  override def describeTo(description: Description) {
    description.appendValue(value.asInstanceOf[AnyRef])
  }
}