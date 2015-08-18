package com.novocode.junit

import sbt.testing._

import scala.collection.mutable

import Ansi._

final class RichLogger private(loggers: Array[Logger], settings: RunSettings) {

  private[this] val currentTestClassName = new mutable.Stack[String]()

  def this(loggers: Array[Logger], settings: RunSettings,
      testClassName: String) = {
    this(loggers, settings)
    currentTestClassName.push(testClassName)
  }

  def pushCurrentTestClassName(s: String): Unit =
    currentTestClassName.push(s)

  def popCurrentTestClassName(): Unit =
    if(currentTestClassName.size > 1) currentTestClassName.pop()

  def debug(s: String): Unit = {
    for (l <- loggers) {
      if (settings.color && l.ansiCodesSupported()) l.debug(s)
      else l.debug(filterAnsi(s))
    }
  }

  def error(s: String): Unit = {
    for(l <- loggers) {
      if(settings.color && l.ansiCodesSupported()) l.error(s)
      else l.error(filterAnsi(s))
    }
  }

  def error(s: String, t: Throwable): Unit = {
    error(s)
    if (t != null && (settings.logAssert || !t.isInstanceOf[AssertionError]))
      logStackTrace(t)
  }

  def info(s: String): Unit = {
    for(l <- loggers) {
      if (settings.color && l.ansiCodesSupported()) l.info(s)
      else l.info(filterAnsi(s))
    }
  }

  def warn(s: String): Unit = {
    for(l <- loggers) {
      if (settings.color && l.ansiCodesSupported()) l.warn(s)
      else l.warn(filterAnsi(s))
    }
  }

  private def logStackTrace(t: Throwable): Unit = {
    val trace = t.getStackTrace.dropWhile {
      p =>
        p.getFileName.contains("StackTrace.scala") ||
        p.getFileName.contains("Throwables.scala")
    }
    val testClassName = currentTestClassName.head
    val testFileName = {
      if (settings.color) findTestFileName(trace, testClassName)
      else null
    }
    val i = trace.indexWhere(p => p.getFileName.contains("JUnitExecuteTest.scala")) - 1
    val m = if (i > 0) i else trace.length
    logStackTracePart(trace, m, trace.length - m, t, testClassName, testFileName)
  }

  private def logStackTracePart(trace: Array[StackTraceElement], m: Int,
      framesInCommon: Int, t: Throwable, testClassName: String,
      testFileName: String): Unit = {
    val m0 = m
    var m2 = m
    var top = 0
    var i = top
    while (i <= m2) {
      if(trace(i).toString.startsWith("org.junit.") ||
          trace(i).toString.startsWith("org.hamcrest.")) {
        if (i == top) {
          top += 1
        } else {
          m2 = i - 1
          var break = false
          while (m2 > top && !break) {
            val s = trace(m2).toString
            if(!s.startsWith("java.lang.reflect.") &&
                !s.startsWith("sun.reflect."))
              break = true
            else
              m2 -= 1
          }
          i = m2 // break
        }
      }
      i += 1
    }

    for (i <- top to m2)
      error("    at " + stackTraceElementToString(trace(i),
          testClassName, testFileName))
    if(m0 != m2) {
      // skip junit-related frames
      error("    ...")
    } else if(framesInCommon != 0) {
      // skip frames that were in the previous trace too
      error("    ... " + framesInCommon + " more")
    }
    logStackTraceAsCause(trace, t.getCause, testClassName, testFileName)
  }

  private def logStackTraceAsCause(causedTrace: Array[StackTraceElement],
      t: Throwable, testClassName: String, testFileName: String): Unit = {
    if (t != null) {
      val trace = t.getStackTrace
      var m = trace.length - 1
      var n = causedTrace.length - 1
      while (m >= 0 && n >= 0 && trace(m) == causedTrace(n)) {
        m -= 1
        n -= 1
      }
      error("Caused by: " + t)
      logStackTracePart(trace, m, trace.length - 1 - m, t, testClassName, testFileName)
    }
  }

  private def findTestFileName(trace: Array[StackTraceElement], testClassName: String): String = {
    trace.collectFirst {
      case e if testClassName.equals(e.getClassName) => e.getFileName
    }.orNull
  }

  private def stackTraceElementToString(e: StackTraceElement,
      testClassName: String, testFileName: String): String = {
    val highlight = settings.color && (
      testClassName == e.getClassName ||
        (testFileName != null && testFileName == e.getFileName)
      )
    val b = new StringBuilder
    b.append(settings.decodeName(e.getClassName + '.' + e.getMethodName))
    b.append('(')

    if(e.isNativeMethod) {
      b.append(c("Native Method", if (highlight) TESTFILE2 else null))
    } else if(e.getFileName == null) {
      b.append(c("Unknown Source", if (highlight) TESTFILE2 else null))
    } else {
      b.append(c(e.getFileName, if (highlight) TESTFILE1 else null))
      if(e.getLineNumber >= 0)
        b.append(':').append(c(String.valueOf(e.getLineNumber), if (highlight) TESTFILE2 else null))
    }
    b.append(')').toString()
  }
}
