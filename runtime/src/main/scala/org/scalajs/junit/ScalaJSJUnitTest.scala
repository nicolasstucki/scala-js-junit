package org.scalajs.junit

import java.lang.annotation.Annotation

import org.junit.FixMethodOrder

import scala.scalajs.js.annotation.JSExportDescendentObjects
import scala.scalajs.js.annotation.JSExportDescendentClasses

case class MethodMetadata(name: String, id: String, annotations: List[Annotation]) {

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

  def getTestAnnotation(): Option[org.junit.Test] =
    annotations.collectFirst { case test: org.junit.Test => test }

  def getIgnoreAnnotation(): Option[org.junit.Ignore] =
    annotations.collectFirst { case ign: org.junit.Ignore => ign }
}

case class ClassMetadata(
    annotations: List[Annotation],
    moduleAnnotations: List[Annotation],
    methods: List[MethodMetadata],
    moduleMethods: List[MethodMetadata]) {

  def testMethods: List[MethodMetadata] = {
    val fixMethodOrderAnnotation = getFixMethodOrderAnnotation()
    val methodSorter = fixMethodOrderAnnotation.value
    val tests = methods.filter(_.hasTestAnnotation)
    tests.sortWith((a, b) => methodSorter.comparator.lt(a.name, b.name))
  }

  def beforeMethod: Option[MethodMetadata] =
    methods.find(_.hasBeforeAnnotation)

  def afterMethod: Option[MethodMetadata] =
    methods.find(_.hasAfterAnnotation)

  def beforeClassMethod: Option[MethodMetadata] =
    moduleMethods.find(_.hasBeforeClassAnnotation)

  def afterClassMethod: Option[MethodMetadata] =
    moduleMethods.find(_.hasAfterClassAnnotation)

  def getFixMethodOrderAnnotation(): FixMethodOrder = {
    annotations.collectFirst {
      case fmo: FixMethodOrder => fmo
    }.getOrElse(new FixMethodOrder)
  }

  override def toString: String = {
    def mkSt(xs: List[AnyRef]) =
      if (xs.isEmpty) "Nil"
      else "List(\n" + xs.map(x => s"    $x").mkString(",\n") + ")"
    s"""
      |ClassMetadata(
      |  annotations = ${mkSt(annotations)},
      |  moduleAnnotations = ${mkSt(moduleAnnotations)},
      |  methods = ${mkSt(methods)},
      |  moduleMethods = ${mkSt(moduleMethods)}
      |)
    """.stripMargin
  }
}

@JSExportDescendentObjects
trait ScalaJSJUnitTestMetadata {
  def scalajs$junit$metadata(): ClassMetadata
  def scalajs$junit$newInstance(): ScalaJSJUnitTest
  def scalajs$junit$invoke(methodId: String): Unit
}

@JSExportDescendentObjects
@JSExportDescendentClasses
trait ScalaJSJUnitTest {
  def scalajs$junit$invoke(methodId: String): Unit
}
