package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD))
class Test extends Annotation {
  def annotationType(): Class[Test] = classOf[Test]
}
