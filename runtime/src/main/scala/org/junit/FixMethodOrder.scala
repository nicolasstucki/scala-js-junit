/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit

import java.lang.annotation._

import org.junit.runners.MethodSorters

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.TYPE))
class FixMethodOrder(val value: MethodSorters = MethodSorters.DEFAULT)
    extends Annotation {
  def annotationType(): Class[Annotation] =
    classOf[FixMethodOrder].asInstanceOf[Class[Annotation]]
}
