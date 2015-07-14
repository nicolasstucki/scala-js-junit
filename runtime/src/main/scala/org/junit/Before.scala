package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD))
class Before extends Annotation {
  def annotationType(): Class[Annotation] =
    classOf[Before].asInstanceOf[Class[Annotation]]
}
