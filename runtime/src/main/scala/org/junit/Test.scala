package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD))
class Test(expected: Class[_ <: Throwable] = None.getClass, timeout: Long = 0L)
    extends Annotation {
  def annotationType(): Class[Test] = classOf[Test]
}

object Test {
  @SerialVersionUID(1L)
  final class None private()
}
