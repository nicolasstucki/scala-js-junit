package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD))
class AfterClass extends Annotation {
  def annotationType(): Class[Annotation] =
    classOf[AfterClass].asInstanceOf[Class[Annotation]]
}
