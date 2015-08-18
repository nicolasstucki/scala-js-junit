/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit

import java.lang.annotation._

import scala.scalajs.js.annotation.JSExport

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD))
@JSExport
case class Test(expected: Class[_ <: Throwable] = classOf[Test.None],
    timeout: Long = 0L) extends Annotation {
  def annotationType (): Class[Annotation] =
    classOf[Test].asInstanceOf[Class[Annotation]]
}

object Test {
  @SerialVersionUID(1L)
  final class None private() extends Throwable
}
