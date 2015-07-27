/*
 * Ported from https://github.com/junit-team/junit
 */
package org.junit

import java.lang.annotation._

@Retention(RetentionPolicy.RUNTIME)
@Target(Array(ElementType.METHOD))
class After extends Annotation {
  def annotationType(): Class[Annotation] =
    classOf[After].asInstanceOf[Class[Annotation]]
}
