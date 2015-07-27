package com.novocode.junit

import com.novocode.junit.Ansi._

import java.util.HashSet

import scala.util.Try

//import org.junit.runner.Description

class RunSettings private(val color: Boolean, val decodeScalaNames: Boolean,
    val quiet: Boolean, val verbose: Boolean, val logAssert: Boolean,
    val logExceptionClass: Boolean) {

  private val ignoreRunnersSet = new HashSet[String]

  def this(color: Boolean, decodeScalaNames: Boolean, quiet: Boolean,
      verbose: Boolean, logAssert: Boolean, ignoreRunners: String,
    logExceptionClass: Boolean) {
    this(color, decodeScalaNames, quiet, verbose, logAssert, logExceptionClass)
    for (s <- ignoreRunners.split(","))
      ignoreRunnersSet.add(s.trim)
  }

  def decodeName(name: String): String =
    if (decodeScalaNames) RunSettings.decodeScalaName(name) else name

  //  def buildInfoName(desc: Description): String =
  //    buildColoredName(desc, NNAME1, NNAME2, NNAME3)
  //
  //  def buildErrorName(desc: Description): String =
  //    buildColoredName(desc, ENAME1, ENAME2, ENAME3)
  //
  //  def buildPlainName(desc: Description): String =
  //    buildColoredName(desc, null, null, null)

  def buildColoredMessage(t: Throwable, c1: String): String = {
    if (t == null) "null" else {
      if (!logExceptionClass || (!logAssert && t.isInstanceOf[AssertionError])) {
        t.getMessage
      } else {
        val b = new StringBuilder()
        val cn = decodeName(t.getClass.getName)
        val pos1 = cn.indexOf('$')
        val pos2 = {
          if (pos1 == -1) cn.lastIndexOf('.')
          else cn.lastIndexOf('.', pos1)
        }
        if (pos2 == -1) b.append(c(cn, c1))
        else {
          b.append(cn.substring(0, pos2))
          b.append('.')
          b.append(c(cn.substring(pos2 + 1), c1))
        }
        b.append(": ").append(t.getMessage)
        b.toString()
      }
    }
  }

  def buildInfoMessage(t: Throwable): String =
    buildColoredMessage(t, NNAME2)

  def buildErrorMessage(t: Throwable): String =
    buildColoredMessage(t, ENAME2)

  //  private def buildColoredName(desc: Description, c1: String, c2: String, c3: String): String = {
  //    val b = new StringBuilder
  //
  //    val cn = RunSettings.decodeName(desc.getClassName)
  //    val pos1 = cn.indexOf('$')
  //    val pos2 = {
  //      if (pos1 == -1) cn.lastIndexOf('.')
  //      else cn.lastIndexOf('.', pos1)
  //    }
  //    if (pos2 == -1) b.append(c(cn, c1))
  //    else {
  //      b.append(cn.substring(0, pos2))
  //      b.append('.')
  //      b.append(c(cn.substring(pos2 + 1), c1))
  //    }
  //
  //    val m = desc.getMethodName()
  //    if (m != null) {
  //      b.append('.')
  //      val mpos1 = m.lastIndexOf('[')
  //      val mpos2 = m.lastIndexOf(']')
  //      if(mpos1 == -1 || mpos2 < mpos1) b.append(c(decodeName(m), c2))
  //      else {
  //        b.append(c(decodeName(m.substring(0, mpos1)), c2))
  //        b.append('[')
  //        b.append(c(m.substring(mpos1 + 1, mpos2), c3))
  //        b.append(']')
  //      }
  //    }
  //
  //    b.toString()
  //  }
  //
  //  def ignoreRunner(cln: String): String =
  //    ignoreRunners.contains(cln)
}

object RunSettings {
  private[RunSettings] def decodeScalaName(name: String): String =
    Try(scala.reflect.NameTransformer.decode(name)).getOrElse(name)
}