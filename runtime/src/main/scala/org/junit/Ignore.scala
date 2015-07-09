package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD, ElementType.TYPE))
class Ignore(val value: java.lang.String = "") extends Annotation {
  def annotationType(): Class[Before] = classOf[Before]
}
