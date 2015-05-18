package org.hamcrest.internal

import org.hamcrest.SelfDescribing

import java.util.Iterator

class SelfDescribingValueIterator[T](
    values: Iterator[T]
  ) extends Iterator[SelfDescribing] {

  override def hasNext(): Boolean = values.hasNext

  override def next(): SelfDescribing = new SelfDescribingValue(values.next)

  override def remove() {
    values.remove()
  }
}