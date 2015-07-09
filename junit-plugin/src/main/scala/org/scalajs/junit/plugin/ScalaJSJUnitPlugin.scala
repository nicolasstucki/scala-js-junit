package org.scalajs.junit.plugin

import org.junit.{BeforeClass, AfterClass}

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
          def classHasJUnitAnnotations(clazz: ClassDef): Boolean = {
            clazz.impl.body.exists {
              case ddef: DefDef => hasJUnitMethodAnnotation(ddef)
              case _            => false
            }
          }
          val newStats = tree.stats map {
            case cldef: ClassDef if classHasJUnitAnnotations(cldef) =>
              transformTestClass(cldef)

            case t => t
          }

          val newPackage = tree.copy(stats = newStats)
          newPackage.setSymbol(tree.symbol)
          newPackage

        case _ =>
          super.transform(tree)
      }

      def transformTestClass(clazz: ClassDef): Tree = {

        val invokeJUnitMethodDef = mkInvokeJUnitMethodDef(clazz)
        val getJUnitMetadataDef = mkGetJUnitMetadataDef(clazz)

        println(invokeJUnitMethodDef)
        println(getJUnitMetadataDef)

        val newBody = clazz.impl.body :+ invokeJUnitMethodDef :+ getJUnitMetadataDef
        val newParents = clazz.impl.parents :::
            TypeTree(typeOf[org.scalajs.junit.ScalaJSJUnitTest2]) :: Nil
        val newImpl = clazz.impl.copy(parents = newParents, body = newBody)
        val newClazz = {
          val newClazz =
              gen.mkClassDef(clazz.mods, clazz.name, clazz.tparams, newImpl)
          val clazzSym = {
            val clazzSym = clazz.symbol
            val newClazzInfo = {
              val newParentsInfo = clazzSym.info.parents :::
                  typeOf[org.scalajs.junit.ScalaJSJUnitTest2] :: Nil
              val decls = clazzSym.info.decls
              decls.enter(invokeJUnitMethodDef.symbol)
              decls.enter(getJUnitMetadataDef.symbol)
              ClassInfoType(newParentsInfo, decls, clazzSym.info.typeSymbol)
            }
            clazzSym.setInfo(newClazzInfo)
            clazzSym
          }
          newClazz.setSymbol(clazzSym)
          newClazz
        }

        val typedClazz = typer.typedClassDef(newClazz)
        typedClazz
      }

      def jUnitAnnotatedMethods(clDef: ClassDef): List[DefDef] = {
        clDef.impl.body collect {
          case ddef: DefDef if hasJUnitMethodAnnotation(ddef) => ddef
        }
      }

      def mkInvokeJUnitMethodDef(clDef: ClassDef): DefDef = {
        val methods = jUnitAnnotatedMethods(clDef)

        val local = methods.head.symbol

        val invokeJUnitMethodSym = local.cloneSymbol
        invokeJUnitMethodSym.withoutAnnotations
        invokeJUnitMethodSym.setName(TermName("invokeJUnitMethod"))
        invokeJUnitMethodSym.setInfo(typeOf[Unit])
        invokeJUnitMethodSym.addAnnotation(new CompleteAnnotationInfo(
            typeOf[scala.scalajs.js.annotation.JSExport], Nil, Nil))

        val paramSym =
            invokeJUnitMethodSym.newValueParameter(TermName("methodId"))
        paramSym.setInfo(typeOf[java.lang.String])

        val methodNotFound = {
          val msg = gen.mkMethodCall(paramSym, TermName("$plus"), Nil,
            List(Literal(Constant(" not found"))))
          val exceptionConstructor = Select(
            New(TypeTree(typeOf[NoSuchMethodException])),
            termNames.CONSTRUCTOR)
          val exception = Apply(exceptionConstructor, List(msg))
          Throw(exception)
        }

        def mkInvokeJUnitMethodRhs(remainingTestMethods: List[(DefDef, Int)]): Tree = {
          remainingTestMethods match {
            case (method, id) :: tail =>
              val symbol = method.symbol
              val headMethodId = Literal(Constant(id.toString))
              If(
                gen.mkMethodCall(paramSym, TermName("equals"), Nil,
                  List(headMethodId)),
                gen.mkMethodCall(symbol, Nil),
                mkInvokeJUnitMethodRhs(tail))
            case Nil =>
              methodNotFound
          }
        }

        val invokeJUnitMethodRhs =
            atOwner(invokeJUnitMethodSym)(typer.typed(mkInvokeJUnitMethodRhs(methods.zipWithIndex)))
        val param = newValDef(paramSym, EmptyTree)()
        typer.typedDefDef(newDefDef(invokeJUnitMethodSym, invokeJUnitMethodRhs)(
            vparamss = List(List(param))))
      }

      def mkGetJUnitMetadataDef(clDef: ClassDef): DefDef = {
        val methods = jUnitAnnotatedMethods(clDef)

        def mkNewInstance[T: TypeTag](params: List[Tree] = Nil): Tree =
          Apply(Select(New(TypeTree(typeOf[T])), termNames.CONSTRUCTOR), params)

        def liftAnnotations(annotations: List[AnnotationInfo]): List[Tree] = {
          annotations.collect {
            case ann if ann.atp == typeOf[org.junit.Test]        => mkNewInstance[org.junit.Test]()
            case ann if ann.atp == typeOf[org.junit.Before]      => mkNewInstance[org.junit.Before]()
            case ann if ann.atp == typeOf[org.junit.After]       => mkNewInstance[org.junit.After]()
            case ann if ann.atp == typeOf[org.junit.BeforeClass] => mkNewInstance[org.junit.BeforeClass]()
            case ann if ann.atp == typeOf[org.junit.AfterClass]  => mkNewInstance[org.junit.AfterClass]()
            case ann if ann.atp == typeOf[org.junit.Ignore] =>
              mkNewInstance[org.junit.Ignore]() // TODO add argument ann.args
          }
        }

        def defaultMethodMetadata[T: TypeTag](methodAndId: (DefDef, Int)): Tree = {
          val (m, id) = methodAndId
          val annotations = liftAnnotations(m.symbol.annotations)
          Apply(
              Select(New(TypeTree(typeOf[T])), termNames.CONSTRUCTOR),
              List(
                Literal(Constant(m.name.toString)),
                Literal(Constant(id.toString)),
                mkList[java.lang.annotation.Annotation](annotations)
              ))
        }

        def mkList[T: TypeTag](elems: List[Tree]): Tree = {
          val array = ArrayValue(TypeTree(typeOf[T]), elems)
          val castArray = gen.mkCast(array, typeOf[Array[Object]])
          val wrappedArray = gen.mkMethodCall(
              definitions.PredefModule,
              definitions.wrapArrayMethodName(typeOf[Object]),
              Nil, List(castArray))
          gen.mkMethodCall(definitions.List_apply, List(wrappedArray))
        }

        def mkMethodList[T: TypeTag](testMethods: List[DefDef]): Tree =
          mkList[T](testMethods.zipWithIndex.map(defaultMethodMetadata[T]))

        val invokeJUnitMethodRhs = {
          Apply(
              Select(New(TypeTree(typeOf[org.scalajs.junit.TestClass])), termNames.CONSTRUCTOR),
              List(
                  gen.mkNil,
                  mkMethodList[org.scalajs.junit.AnnotatedMethod](methods)))
        }

        val local = methods.head.symbol
        val getJUnitMetadataSym = local.cloneSymbol
        getJUnitMetadataSym.withoutAnnotations
        getJUnitMetadataSym.setName(TermName("getJUnitMetadata"))
        getJUnitMetadataSym.setInfo(MethodType(Nil, typeOf[org.scalajs.junit.TestClass]))

        typer.typedDefDef(newDefDef(getJUnitMetadataSym, invokeJUnitMethodRhs)())
      }

      def hasJUnitMethodAnnotation(ddef: DefDef): Boolean = {
        hasAnnotation[org.junit.Test](ddef) ||
            hasAnnotation[org.junit.Before](ddef) ||
            hasAnnotation[org.junit.After](ddef) ||
            hasAnnotation[org.junit.BeforeClass](ddef) ||
            hasAnnotation[org.junit.AfterClass](ddef) ||
            hasAnnotation[org.junit.Ignore](ddef)
      }

      def hasAnnotation[T: TypeTag](ddef: DefDef): Boolean =
        ddef.symbol.annotations.exists(_.atp == typeOf[T])

    }
  }
}

