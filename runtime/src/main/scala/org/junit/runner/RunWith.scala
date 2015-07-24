package org.junit.runner

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.TYPE))
@Inherited
class RunWith(value: Class[_ <: Runner]) extends Annotation {
  def annotationType(): Class[_ <: Annotation] = classOf[RunWith]
}