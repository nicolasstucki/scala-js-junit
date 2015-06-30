package org.scalajs.junit.plugin

import scala.tools.nsc._
import scala.tools.nsc.plugins.{
Plugin => NscPlugin, PluginComponent => NscPluginComponent
}

class ScalaJSJUnitPlugin(val global: Global) extends NscPlugin {
  selfPlugin =>

  val name: String = "Scala.js JUnit plugin"

  val components: List[NscPluginComponent] =
    List(ScalaJSJUnitPluginComponents)

  val description: String = "Adapts JUnit test classes to Scala.js"

  object ScalaJSJUnitPluginComponents extends plugins.PluginComponent with transform.Transform {

    protected def newTransformer(unit: global.CompilationUnit): global.Transformer =
      new ScalaJSJUnitPluginTransformer

    val global: Global = selfPlugin.global

    val phaseName: String = "junit-inject"

    val runsAfter: List[String] = List("mixin")

    override val runsBefore: List[String] = List("jscode")


    class ScalaJSJUnitPluginTransformer extends global.Transformer {
      override def transform(tree: global.Tree): global.Tree = tree match {
        case _ => {
//          println(s"transform(tree)")
          super.transform(tree)
        }
      }
    }
  }
}

