package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.FIELD, ElementType.METHOD))
trait ClassRule extends Annotation {
  def annotationType(): Class[ClassRule] = classOf[ClassRule]
}