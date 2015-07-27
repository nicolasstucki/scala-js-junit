package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.FIELD, ElementType.METHOD))
trait Rule extends Annotation {
  def annotationType(): Class[Rule] = classOf[Rule]
}