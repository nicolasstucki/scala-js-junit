package org.scalajs.junit

import java.lang.annotation.Annotation

import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

import scala.scalajs.js.annotation.JSExportDescendentClasses

case class AnnotatedMethod(name: String, id: String, annotations: List[Annotation]) {

  def hasTestAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.Test])

  def hasBeforeAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.Before])

  def hasAfterAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.After])

  def getIgnoreAnnotation(): Option[org.junit.Ignore] =
    annotations.collectFirst { case ign: org.junit.Ignore => ign }

  def hasBeforeClassAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.BeforeClass])

  def hasAfterClassAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.AfterClass])
}

case class TestClass(
    annotations: List[Annotation],
    methods: List[AnnotatedMethod]) {

  def testMethods: List[AnnotatedMethod] = {
    val fixMethodOrderAnnotation = getFixMethodOrderAnnotation()
    println("Sorting methods with " + fixMethodOrderAnnotation)
    val methodOrdering = fixMethodOrderAnnotation.value.comparator
    methods.filter(_.hasTestAnnotation).sortWith((a, b) => methodOrdering.lt(a.name, b.name))
  }

  def beforeMethods: List[AnnotatedMethod] =
    methods.filter(_.hasBeforeAnnotation)

  def afterMethods: List[AnnotatedMethod] =
    methods.filter(_.hasAfterAnnotation)

  def beforeClassMethods: List[AnnotatedMethod] =
    methods.filter(_.hasBeforeClassAnnotation)

  def afterClassMethods: List[AnnotatedMethod] =
    methods.filter(_.hasAfterClassAnnotation)

  def getFixMethodOrderAnnotation(): FixMethodOrder = {
    annotations.collectFirst {
      case fmo: FixMethodOrder => fmo
    }.getOrElse(new FixMethodOrder)
  }

}

@JSExportDescendentClasses
trait ScalaJSJUnitTest {

  def invokeJUnitMethod$(methodId: String): Unit

  def getJUnitMetadata$(): TestClass

}

@JSExportDescendentClasses
trait ScalaJSJUnitTest2