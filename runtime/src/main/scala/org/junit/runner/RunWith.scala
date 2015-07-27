/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit.runner

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.TYPE))
@Inherited
class RunWith(value: Class[_ <: Runner]) extends Annotation {
  override def annotationType(): Class[Annotation] =
    classOf[RunWith].asInstanceOf[Class[Annotation]]
}
