package org.scalajs.junit

import java.lang.annotation.Annotation

import org.junit.FixMethodOrder

import scala.scalajs.js.annotation.JSExportDescendentObjects

@JSExportDescendentObjects
trait JUnitTestMetadata {
  def scalajs$junit$metadata(): JUnitClassMetadata
  def scalajs$junit$newInstance(): ScalaJSJUnitTest
  def scalajs$junit$invoke(methodId: String): Unit
}

case class JUnitMethodMetadata(name: String, id: String,
    annotations: List[Annotation]) {

  def hasTestAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.Test])

  def hasBeforeAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.Before])

  def hasAfterAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.After])

  def hasBeforeClassAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.BeforeClass])

  def hasAfterClassAnnotation: Boolean =
    annotations.exists(_.isInstanceOf[org.junit.AfterClass])

  def getTestAnnotation (): Option[org.junit.Test] =
    annotations.collectFirst { case test: org.junit.Test => test }

  def getIgnoreAnnotation (): Option[org.junit.Ignore] =
    annotations.collectFirst { case ign: org.junit.Ignore => ign }
}

case class JUnitClassMetadata(annotations: List[Annotation],
    moduleAnnotations: List[Annotation], methods: List[JUnitMethodMetadata],
    moduleMethods: List[JUnitMethodMetadata]) {

  def testMethods: List[JUnitMethodMetadata] = {
    val fixMethodOrderAnnotation = getFixMethodOrderAnnotation()
    val methodSorter = fixMethodOrderAnnotation.value
    val tests = methods.filter(_.hasTestAnnotation)
    tests.sortWith((a, b) => methodSorter.comparator.lt(a.name, b.name))
  }

  def beforeMethod: List[JUnitMethodMetadata] =
    methods.filter(_.hasBeforeAnnotation)

  def afterMethod: List[JUnitMethodMetadata] =
    methods.filter(_.hasAfterAnnotation)

  def beforeClassMethod: List[JUnitMethodMetadata] =
    moduleMethods.filter(_.hasBeforeClassAnnotation)

  def afterClassMethod: List[JUnitMethodMetadata] =
    moduleMethods.filter(_.hasAfterClassAnnotation)

  def getFixMethodOrderAnnotation(): FixMethodOrder = {
    annotations.collectFirst {
      case fmo: FixMethodOrder => fmo
    }.getOrElse(new FixMethodOrder)
  }
}
