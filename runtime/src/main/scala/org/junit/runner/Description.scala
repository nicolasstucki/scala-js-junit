package org.junit.runner

import java.lang.annotation.Annotation
import java.{util => ju}
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.regex.Pattern

import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class Description private[Description] (testClass: Class[_], displayName: String,
    private[Description] val uniqueId: Serializable, annotations: Annotation*)
    extends Serializable {

  private def initChecks() = {
    if (displayName == null || displayName.length == 0)
      throw new IllegalArgumentException("The display name must not be empty.")
    else if (uniqueId == null)
      throw new IllegalArgumentException("The unique id must not be null.")
  }

  initChecks()

  private final val fChildren = new ConcurrentLinkedQueue[Description]()
  private var /* write-once */ fTestClass: Class[_] = _

  private[Description] def this(clazz: Class[_], displayName: String,
      annotations: Annotation*) = {
    this(clazz, displayName, displayName.asInstanceOf[Serializable], annotations: _*)
  }

  def getDisplayName(): String = displayName

  def addChild(description: Description): Unit = fChildren.add(description)

  def getChildren(): ju.ArrayList[Description] =
    new ju.ArrayList[Description](fChildren)

  def isSuite(): Boolean = !isTest

  def isTest(): Boolean = fChildren.isEmpty

  def testCount(): Int = {
    if (isTest()) 1
    else fChildren.map(_.testCount).sum
  }

  override def hashCode(): Int = uniqueId.hashCode

  override def equals(obj: Any): Boolean = {
    obj match {
      case obj: Description => uniqueId == obj.uniqueId
      case _                => false
    }
  }

  override def toString(): String = getDisplayName()

  def isEmpty(): Boolean = equals(Description.EMPTY)

  def childlessCopy(): Description =
    new Description(testClass, displayName, annotations: _*)

  def getAnnotation[T <: Annotation](annotationType: Class[T]): T = {
    annotations.find(_.annotationType() == annotationType).fold(
        null.asInstanceOf[T])(annotationType.cast(_))
  }

  def getAnnotations(): ju.Collection[Annotation] =
    ju.Arrays.asList(annotations: _*)

  def getTestClass(): Class[_] = {
    if (fTestClass != null) {
      fTestClass
    } else {
      val name = getClassName()
      if (name == null) {
        null
      } else {
        try {
          ???
//          fTestClass = Class.forName(name, false, getClass().getClassLoader())
//          fTestClass
        } catch {
          case _: ClassNotFoundException => null
        }
      }
    }
  }

  def getClassName(): String = {
    if (fTestClass != null) fTestClass.getName()
    else methodAndClassNamePatternGroupOrDefault(2, toString())
  }

  def getMethodName(): String =
    methodAndClassNamePatternGroupOrDefault(1, null)

  private def methodAndClassNamePatternGroupOrDefault(group: Int,
      defaultString: String): String = {
    val matcher = Description.METHOD_AND_CLASS_NAME_PATTERN.matcher(toString())
    if(matcher.matches) matcher.group(group) else defaultString
  }
}

object Description {
  private[Description] final val METHOD_AND_CLASS_NAME_PATTERN =
    Pattern.compile("([\\s\\S]*)\\((.*)\\)")

  final val EMPTY = new Description(null, "No Tests")

  final val TEST_MECHANISM = new Description(null, "Test mechanism")
}
