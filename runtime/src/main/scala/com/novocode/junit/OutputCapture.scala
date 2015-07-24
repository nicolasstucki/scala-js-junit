package com.novocode.junit

import java.io.ByteArrayOutputStream
import java.io.PrintStream

object OutputCapture {
  def start(): OutputCapture = new OutputCapture()

  private def getScalaOut(): PrintStream = {
    try {
      scala.Console.out
    } catch {
      case t: Throwable => null
    }
  }

  private def setScalaOut(p: PrintStream): Boolean = {
    try {
      scala.Console.setOut(p)
      true
    } catch {
      case t: Throwable => false
    }
  }
}

final class OutputCapture private[OutputCapture]() {
  private val originalOut = System.out
  private val originalScalaOut = OutputCapture.getScalaOut()
  private val buffer = new ByteArrayOutputStream()
  private val prBuffer = new PrintStream(buffer, true)
  private val scalaOutSet: Boolean = OutputCapture.setScalaOut(prBuffer)

  System.out.flush()
  System.setOut(prBuffer)

  def stop(): Unit = {
    System.out.flush()
    System.setOut(originalOut)
    if (scalaOutSet) OutputCapture.setScalaOut(originalScalaOut)
  }

  def replay(): Unit = {
    System.out.write(buffer.toByteArray)
    System.out.flush()
  }
}