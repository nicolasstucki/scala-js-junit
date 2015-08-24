package org.scalajs.junit.plugin

import org.scalajs.junit.{JUnitTestMetadata, JUnitClassMetadata,
JUnitMethodMetadata, ScalaJSJUnitTest}

import scala.tools.nsc._
import scala.tools.nsc.plugins.{
Plugin => NscPlugin, PluginComponent => NscPluginComponent
}

class ScalaJSJUnitPlugin(val global: Global) extends NscPlugin {
  selfPlugin =>

  val name: String = "Scala.js JUnit plugin"

  val components: List[NscPluginComponent] =
    List(ScalaJSJUnitPluginComponent)

  val description: String = "Adapts JUnit test classes to Scala.js"

  object ScalaJSJUnitPluginComponent extends plugins.PluginComponent
      with transform.Transform {

    protected def newTransformer(
        unit: global.CompilationUnit): global.Transformer = {
      new ScalaJSJUnitPluginTransformer
    }

    val global: Global = selfPlugin.global
    val phaseName: String = "junit-inject"
    val runsAfter: List[String] = List("mixin")
    override val runsBefore: List[String] = List("jscode")

    class ScalaJSJUnitPluginTransformer extends global.Transformer {
      import global._

      type ScalaJSPlugin = NscPlugin {
        def PrepInteropComponent: AnyRef {
          def genExportMember(sym: Symbol): List[Tree]
          def registerModuleExports(sym: Symbol): Unit
          def genNamedExport(defSym: Symbol, jsName: String, pos: Position): Unit
        }
      }

      lazy val scalaJSPlugin = {
        global.plugins.collectFirst {
          case pl if pl.getClass.getName == "org.scalajs.core.compiler.ScalaJSPlugin" => pl
        }.get.asInstanceOf[ScalaJSPlugin]
      }

      override def transform(tree: Tree): Tree = tree match {
        case tree: PackageDef =>
          def classHasJUnitAnnotations(clazz: ClassDef): Boolean = {
            clazz.impl.body.exists {
              case ddef: DefDef => hasAnnotation[org.junit.Test](ddef)
              case _            => false
            }
          }
          val newStats = tree.stats.groupBy {
            case clDef: ClassDef => clDef.name
            case _ => "not a class def"
          }.iterator.flatMap {
            case ("not a class def", xs) => xs
            case (className , x :: xs) =>
              val clDef1 = x.asInstanceOf[ClassDef]
              val clDef2Option = xs match {
                case Nil     => None
                case y :: ys =>
                  if (ys.isEmpty) Some(y.asInstanceOf[ClassDef])
                  else None
              }
              val (clDefOption, mlDefOption) = {
                if (clDef1.mods.hasFlag(256L /*FIXME find flag alias*/))
                  (clDef2Option, Some(clDef1))
                else (Some(clDef1), clDef2Option)
              }

              if (clDefOption.fold(false)(classHasJUnitAnnotations) ||
                  mlDefOption.fold(false)(classHasJUnitAnnotations)) {
                val transformedClDef = clDefOption match {
                  case Some(clDef) => transformTestClass(clDef)

                  case None =>
                    throw new ClassNotFoundException(
                        mlDefOption.get.name.toString)
                }
                val hookClass = mkHookClass(transformedClDef, mlDefOption)
                hookClass :: transformedClDef :: mlDefOption.toList
              }
              else x :: xs

            case (_, Nil) => Nil
          }

          val newPackage = tree.copy(stats = newStats.toList)
          newPackage.setSymbol(tree.symbol)
          newPackage

        case _ =>
          super.transform(tree)
      }

      def getLocalMethodSymbol(clazz: ClassDef): Symbol = {
        clazz.impl.body.collectFirst {
          case dDef: DefDef if hasAnnotation[org.junit.Test](dDef) => dDef
        }.get.symbol
      }

      def transformTestClass(clazz: ClassDef): ClassDef = {
        inform("Scala.js JUnit plugin: transforming " + clazz.name)
        val invokeJUnitMethodDef = {
          val annotatedMethods = jUnitAnnotatedMethods(clazz)
          val localMethodSymbol = getLocalMethodSymbol(clazz)
          mkInvokeJUnitMethodDef(annotatedMethods, localMethodSymbol)
        }

        val newBody = invokeJUnitMethodDef :: clazz.impl.body
        val newParents = clazz.impl.parents :::
            TypeTree(typeOf[ScalaJSJUnitTest]) :: Nil
        val newImpl = clazz.impl.copy(parents = newParents, body = newBody)
        val newClazz = {
          val newClazz =
            gen.mkClassDef(Modifiers(), clazz.name, Nil, newImpl)
          val clazzSym = {
            val clazzSym = clazz.symbol
            val newClazzInfo = {
              val newParentsInfo = clazzSym.info.parents :::
                typeOf[org.scalajs.junit.ScalaJSJUnitTest]  :: Nil
              val decls = clazzSym.info.decls
              decls.enter(invokeJUnitMethodDef.symbol)
              ClassInfoType(newParentsInfo, decls, clazzSym.info.typeSymbol)
            }
            clazzSym.setInfo(newClazzInfo)
            clazzSym
          }
          newClazz.setSymbol(clazzSym)
          newClazz
        }

        typer.typedClassDef(newClazz).asInstanceOf[ClassDef]
      }

      def mkHookClass(clazz: ClassDef, mlDefOption: Option[ClassDef]): Tree = {
        val clazzSym = clazz.symbol.cloneSymbol
        val getJUnitMetadataDef = mkGetJUnitMetadataDef (clazz, mlDefOption)
        val newInstanceDef = mkNewInstanceDef(clazz, mlDefOption)
        val invokeJUnitMethodDef = {
          val annotatedMethods = mlDefOption.fold[List[DefDef]](Nil)(jUnitAnnotatedMethods)
          val localMethodSymbol = (annotatedMethods ::: getMethods(clazz)).head.symbol
          localMethodSymbol.setInfo(localMethodSymbol.info.atOwner(clazzSym))
          mkInvokeJUnitMethodDef(annotatedMethods, localMethodSymbol)
        }

        val newBody = getJUnitMetadataDef :: newInstanceDef :: invokeJUnitMethodDef :: Nil
        val newParents = TypeTree(typeOf[java.lang.Object]) ::
            TypeTree(typeOf[JUnitTestMetadata]) :: Nil
        val newImpl = clazz.impl.copy(parents = newParents, body = newBody)
        val newClazz = {
          val newClassName = TypeName(clazz.name.toString + "$scalajs$junit$hook")
          val newClazz = gen.mkClassDef(Modifiers(256L /*FIXME find flag alias*/),
              newClassName, Nil, newImpl)
          clazzSym.flags += 256L /*FIXME find flag alias*/
          clazzSym.withoutAnnotations
          clazzSym.setName(newClassName)
          val newClazzInfo = {
            val newParentsInfo = typeOf[java.lang.Object] ::
                typeOf[JUnitTestMetadata] :: Nil
            val decls = clazzSym.info.decls
            decls.enter(getJUnitMetadataDef.symbol)
            decls.enter(newInstanceDef.symbol)
            decls.enter(invokeJUnitMethodDef.symbol)
            ClassInfoType(newParentsInfo, decls, clazzSym.info.typeSymbol)
          }
          clazzSym.setInfo(newClazzInfo)
          scalaJSPlugin.PrepInteropComponent.registerModuleExports(clazzSym)

          newClazz.setSymbol(clazzSym)
          newClazz
        }
        typer.typedClassDef(newClazz)
      }

      def jUnitAnnotatedMethods(clDef: ClassDef): List[DefDef] = {
        clDef.impl.body.collect {
          case ddef: DefDef if hasJUnitMethodAnnotation(ddef) => ddef
        }
      }

      def getMethods(clazzDef: ClassDef): List[DefDef] =
        clazzDef.impl.body.collect { case dDef: DefDef => dDef }

      def mkInvokeJUnitMethodDef(methods: List[DefDef],
          localMethodSymbol: Symbol): DefDef = {
        val invokeJUnitMethodSym = localMethodSymbol.cloneSymbol
        invokeJUnitMethodSym.withoutAnnotations
        invokeJUnitMethodSym.setName(TermName("scalajs$junit$invoke"))

        val paramSym =
          invokeJUnitMethodSym.newValueParameter(TermName("methodId"))
        paramSym.setInfo(typeOf[java.lang.String])

        invokeJUnitMethodSym.setInfo(MethodType(List(paramSym), typeOf[Unit]))

        val methodNotFound = {
          val msg = gen.mkMethodCall(paramSym, TermName("$plus"), Nil,
            List(Literal(Constant(" not found"))))
          val exceptionConstructor = Select(
            New(TypeTree(typeOf[NoSuchMethodException])),
            termNames.CONSTRUCTOR)
          val exception = Apply(exceptionConstructor, List(msg))
          Throw(exception)
        }

        def mkInvokeJUnitMethodRhs(
            remainingTestMethods: List[(DefDef, Int)]): Tree = {
          remainingTestMethods match {
            case (method, id) :: tail =>
              val symbol = method.symbol
              val headMethodId = Literal(Constant(id.toString))
              If(
                gen.mkMethodCall(paramSym, TermName("$eq$eq"), Nil,
                  List(headMethodId)),
                gen.mkMethodCall(symbol, Nil),
                mkInvokeJUnitMethodRhs(tail))
            case Nil =>
              methodNotFound
          }
        }
        val invokeJUnitMethodRhs =
          atOwner(invokeJUnitMethodSym)(typer.typed(mkInvokeJUnitMethodRhs(
              methods.zipWithIndex)))
        val param = newValDef(paramSym, EmptyTree)()
        typer.typedDefDef(newDefDef(invokeJUnitMethodSym, invokeJUnitMethodRhs)(
          vparamss = List(List(param))))
      }

      def mkGetJUnitMetadataDef(clDef: ClassDef,
          mlDefOption : Option[ClassDef]): DefDef = {
        val methods = jUnitAnnotatedMethods(clDef)
        val mlMethods = mlDefOption.map(jUnitAnnotatedMethods)

        def mkNewInstance[T: TypeTag](params: List[Tree] = Nil): Tree =
          Apply(Select(New(TypeTree(typeOf[T])), termNames.CONSTRUCTOR), params)

        def liftAnnotations(methodSymbol: Symbol): List[Tree] = {
          val annotations = methodSymbol.annotations
          def lift(t: Tree): Tree = {
            t match {
              case lit: Literal => lit

              case Select(t2, name) => Select(lift(t2), name)

              case ValDef(mod, name, tpt, rhs) =>
                ValDef(mod, name, tpt, lift(rhs))

              case _ => t
            }
          }
          def liftList(params: List[Tree]): List[Tree] =
            params.map(lift)

          annotations.collect {
            case ann if ann.atp == typeOf[org.junit.Test] =>
              ann.original match {
                case Block(stats, _) =>
                  // TODO: Add support for @Test(timeout=...)
                  // val newStats = stats
                  // val newArgs = liftList(ann.args)
                  // val newAnn = mkNewInstance[org.junit.Test](newArgs)
                  // Block(newStats, newAnn)
                  throw new UnsupportedOperationException(
                      "@Test(timeout = ...) is not yet supported in " +
                      "Scala.js JUnit Framework")

                case _ => mkNewInstance[org.junit.Test](liftList(ann.args))
              }

            case ann if ann.atp == typeOf[org.junit.Before] =>
              mkNewInstance[org.junit.Before]()

            case ann if ann.atp == typeOf[org.junit.After] =>
              mkNewInstance[org.junit.After]()

            case ann if ann.atp == typeOf[org.junit.BeforeClass] =>
              mkNewInstance[org.junit.BeforeClass]()

            case ann if ann.atp == typeOf[org.junit.AfterClass] =>
              mkNewInstance[org.junit.AfterClass]()

            case ann if ann.atp == typeOf[org.junit.Ignore] =>
              mkNewInstance[org.junit.Ignore](liftList(ann.args))

            case ann if ann.atp == typeOf[org.junit.FixMethodOrder] =>
              // TODO: Add support for @FixMethodOrder(timeout=...)
              // mkNewInstance[org.junit.FixMethodOrder](liftList(ann.args))
              throw new UnsupportedOperationException(
                  "@FixMethodOrder(...) is not yet supported in " +
                    "Scala.js JUnit Framework")

          }
        }

        def defaultMethodMetadata[T: TypeTag](methodAndId: (DefDef, Int)): Tree = {
          val (m, id) = methodAndId
          val annotations = liftAnnotations(m.symbol)
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

        val getJUnitMethodRhs = {
          Apply(
              Select(
                  New(TypeTree(typeOf[JUnitClassMetadata])),
                  termNames.CONSTRUCTOR),
              List(
                  mkList[java.lang.annotation.Annotation](
                      liftAnnotations(clDef.symbol)),
                  gen.mkNil,
                  mkMethodList[JUnitMethodMetadata](methods),
                  mlMethods.
                    map(mkMethodList[JUnitMethodMetadata]).getOrElse(gen.mkNil)
              ))
        }

        val local = methods.head.symbol
        val getJUnitMetadataSym = local.cloneSymbol
        getJUnitMetadataSym.withoutAnnotations
        getJUnitMetadataSym.setName(TermName("scalajs$junit$metadata"))
        getJUnitMetadataSym.setInfo(MethodType(Nil, typeOf[JUnitClassMetadata]))

        typer.typedDefDef(newDefDef(getJUnitMetadataSym, getJUnitMethodRhs)())
      }

      def mkNewInstanceDef(clDef: ClassDef, mlDefOption : Option[ClassDef]): DefDef = {
        val mkNewInstanceDefRhs = {
          Apply(
            Select(
              New(TypeTree(clDef.symbol.typeConstructor)),
              termNames.CONSTRUCTOR),
            List())
        }
        val localMethod = clDef.impl.body.collectFirst { case dDef: DefDef => dDef }.get
        val mkNewInstanceDefSym = localMethod.symbol.cloneSymbol
        mkNewInstanceDefSym.withoutAnnotations
        mkNewInstanceDefSym.setName(TermName("scalajs$junit$newInstance"))
        mkNewInstanceDefSym.setInfo(MethodType(Nil, typeOf[ScalaJSJUnitTest]))

        typer.typedDefDef(newDefDef(mkNewInstanceDefSym, mkNewInstanceDefRhs)())
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
