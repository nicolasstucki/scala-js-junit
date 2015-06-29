package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD))
class BeforeClass extends Annotation {
  def annotationType(): Class[BeforeClass] = classOf[BeforeClass]
}
