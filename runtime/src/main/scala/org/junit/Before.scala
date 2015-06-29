package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD))
class Before extends Annotation {
  def annotationType(): Class[Before] = classOf[Before]
}
