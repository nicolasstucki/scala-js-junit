/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */
package org.hamcrest.internal

import java.{util => ju}

class ArrayIterator(array: Array[AnyRef], private var currentIndex: Int = 0)
    extends ju.Iterator[AnyRef] {

  def this(array: AnyRef) {
    this(
        array match {
          case arr: Array[AnyRef] => arr
          case _ => throw new IllegalArgumentException("not an array")
        }, 0)
  }

  override def hasNext: Boolean =
    currentIndex < array.length

  override def next(): AnyRef = {
    val _currentIndex = currentIndex
    currentIndex = _currentIndex + 1
    array(_currentIndex)
  }

  override def remove(): Unit =
    throw new UnsupportedOperationException("cannot remove items from an array")
}
