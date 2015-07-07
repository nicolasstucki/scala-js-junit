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


    class ScalaJSJUnitPluginTransformer extends global.Transformer {
      import global._

      override def transform(tree: Tree): Tree = tree match {
        case tree: PackageDef =>
          val (classesWithTests, classesWithoutTests) = tree.stats.partition {
            case clazz: ClassDef => clazz.impl.body.exists(hasTestAnnotation)
          }

          val newClassesWithTests = classesWithTests.map(tree => transformTestClass(tree.asInstanceOf[ClassDef]))

          println(newClassesWithTests)
          val newPackage = tree.copy(stats = classesWithoutTests ++ newClassesWithTests)
          newPackage

        case _ =>
          super.transform(tree)
      }

      def transformTestClass(clazz: ClassDef): ClassDef = {
        val clazzBody = clazz.impl.body

        val testMethods = clazzBody.collect { case m: DefDef if hasTestAnnotation(m) => m }
//        val beforeMethods = clazzBody.collect { case m: DefDef if hasBeforeAnnotation(m) => m }
//        val afterMethods = clazzBody.collect { case m: DefDef if hasAfterAnnotation(m) => m }

//        def liftMethod(to: TermName, m: DefDef): (Tree, Tree) = {
//          val methodRef = m.name
//          val nameStr = methodRef.toString
//          val liftedName = TypeName(nameStr.head.toUpper + nameStr.tail + "Lifted")
//          val name = Literal(Constant(nameStr))
//          val metadata = null
//          val caller = null
//          (metadata, caller)
//        }
//
//        val liftedTestMethods: List[(Tree, Tree)] = testMethods.map(liftMethod(TermName("TestMethod"), _))
//        val liftedBeforeMethods: List[(Tree, Tree)] = beforeMethods.map(liftMethod(TermName("BeforeMethod"), _))
//        val liftedAfterMethods: List[(Tree, Tree)] = afterMethods.map(liftMethod(TermName("AfterMethod"), _))


//        val local = testMethods.head.symbol
//        val invokedMethodSym = local.cloneSymbol
//        invokedMethodSym.setInfo(local.owner.info.memberInfo(local).cloneInfo(invokedMethodSym))

//        val invokeJUnitMethodRhs0 = gen.mkMethodCall(local, Nil)
//        val invokeJUnitMethodRhs1 = atOwner(invokedMethodSym)(typer.typed(invokeJUnitMethodRhs0))

//        val invokeJUnitMethodSym = testMethods.head.symbol.cloneSymbol
//        invokeJUnitMethodSym.withoutAnnotations
//        invokeJUnitMethodSym.setName(TermName("$invokeJUnitMethod$"))
//        invokeJUnitMethodSym.setInfo(local.owner.info.memberInfo(local).cloneInfo(invokeJUnitMethodSym))
//        invokeJUnitMethodSym.setFlag(Flag.FINAL | Flag.SYNTHETIC)

//        val invokeJUnitMethodDef = typer.typed(newDefDef(invokeJUnitMethodSym, gen.mkNil)())
//        val invokeJUnitMethodDef = newDefDef(invokeJUnitMethodSym, invokeJUnitMethodRhs1)()
//        println(invokeJUnitMethodDef)

        val newBody = clazz.impl.body //:+ invokeJUnitMethodDef
        val newImpl = clazz.impl.copy(body = newBody)
        val newClazz = clazz.copy(impl = newImpl)
        newClazz
        gen.mkClassDef(clazz.mods, clazz.name, clazz.tparams, clazz.impl)
//      clazz
      }

      def hasTestAnnotation(tree: Tree): Boolean =
        hasJUnitAnnotation(tree, "Test")

      def hasBeforeAnnotation(tree: Tree): Boolean =
        hasJUnitAnnotation(tree, "Before")

      def hasAfterAnnotation(tree: Tree): Boolean =
        hasJUnitAnnotation(tree, "After")

      def hasBeforeClassAnnotation(tree: Tree): Boolean =
        hasJUnitAnnotation(tree, "BeforeClass")

      def hasAfterClassAnnotation(tree: Tree): Boolean =
        hasJUnitAnnotation(tree, "AfterClass")

      def hasJUnitAnnotation(tree: Tree, name: String): Boolean = tree match {
        case tree: DefDef => tree.toString().contains(s"@org.junit.$name")
        case _            => false // TODO warn user
      }

    }
  }
}

