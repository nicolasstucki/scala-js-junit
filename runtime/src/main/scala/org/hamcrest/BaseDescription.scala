/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */
package org.hamcrest

import java.util.Arrays
import java.lang.String.valueOf
import scala.annotation.tailrec
import org.hamcrest.internal.ArrayIterator
import org.hamcrest.internal.SelfDescribingValueIterator

abstract class BaseDescription extends Description {
  override def appendText(text: String): Description = {
    append(text)
    this
  }

  override def appendDescriptionOf(value: SelfDescribing): Description = {
    value.describeTo(this)
    this
  }

  override def appendValue(value: AnyRef): Description = {
    value match {
      case null                   => append("null")
      case v: String              => append(toJavaSyntax(v))
      case v: java.lang.Character => append('"' + toJavaSyntax(v) + '"')
      case v: java.lang.Short     => append(s"<${descriptionOf(value)}s>")
      case v: java.lang.Long      => append(s"<${descriptionOf(value)}L>")
      case v: java.lang.Float     => append(s"<${descriptionOf(value)}F>")

      case v: Array[AnyRef] =>
        appendValueList("[",", ","]", new ArrayIterator(v))

      case _ => append(s"<${descriptionOf(value)}>")
    }
    this
  }

  private def descriptionOf(value: AnyRef): String = {
    try {
      valueOf(value)
    } catch {
      case _: Exception =>
        s"${value.getClass.getName}@${Integer.toHexString(value.hashCode)}"
    }
  }

  override def appendValueList[T](start: String, separator: String, end: String,
      values: T*): Description = {
    appendValueList(start, separator, end, Arrays.asList(values))
  }

  override def appendValueList[T](start: String, separator: String, end: String,
      values:  java.lang.Iterable[T]): Description = {
    appendValueList(start, separator, end, values.iterator())
  }

  private def appendValueList[T](start: String, separator: String, end: String,
      values: java.util.Iterator[T]): Description = {
    appendList(start, separator, end, new SelfDescribingValueIterator[T](values))
  }

  override def appendList(start: String, separator: String, end: String,
      values: java.lang.Iterable[SelfDescribing]): Description = {
    appendList(start, separator, end, values.iterator())
  }

  private def appendList(start: String, separator: String, end: String,
      i: java.util.Iterator[SelfDescribing]): Description = {
    @tailrec
    def appendElems(separate: Boolean): Unit = {
      if (i.hasNext) {
        if(separate) append(separator)
        appendDescriptionOf(i.next)
        appendElems(true)
      }
    }
    append(start)
    appendElems(false)
    append(end)
    this
  }

  protected def append(str: String) {
    str.foreach(append)
  }

  protected def append(c: Char): Unit

  private def toJavaSyntax(unformatted: String): String = {
    s"'${unformatted.map(toJavaSyntax)}'"
  }

  private def toJavaSyntax(ch: Char): String = {
    ch match {
      case '"'  => "\\\""
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case _    => s"$ch"
    }
  }
}
