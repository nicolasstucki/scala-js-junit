package com.novocode.junit

object Ansi {
  // Standard ANSI sequences
  private[this] val NORMAL = "\u001B[0m"
  private[this] val HIGH_INTENSITY = "\u001B[1m"
  private[this] val LOW_INTESITY = "\u001B[2m"
  private[this] val BLACK = "\u001B[30m"
  private[this] val RED = "\u001B[31m"
  private[this] val GREEN = "\u001B[32m"
  private[this] val YELLOW = "\u001B[33m"
  private[this] val BLUE = "\u001B[34m"
  private[this] val MAGENTA = "\u001B[35m"
  private[this] val CYAN = "\u001B[36m"
  private[this] val WHITE = "\u001B[37m"

  def c(s: String, colorSequence: String): String =
    if(colorSequence == null) s
    else colorSequence + s + NORMAL

  def filterAnsi(s: String): String = {
    if(s == null) return null
    val b = new StringBuilder(s.length)
    val len = s.length
    var i = 0
    while (i < len) {
      val c = s.charAt(i)
      if (c == '\u001B') {
        i += 1
        while (s.charAt(i) != 'm') i += 1
      }
      else b.append(c)
      i += 1
    }
    b.toString()
  }

  val INFO = BLUE
  val ERRCOUNT = RED
  val IGNCOUNT = YELLOW
  val ERRMSG = RED
  val NNAME1 = YELLOW
  val NNAME2 = CYAN
  val NNAME3 = YELLOW
  val ENAME1 = YELLOW
  val ENAME2 = RED
  val ENAME3 = YELLOW
  val TESTFILE1 = MAGENTA
  val TESTFILE2 = YELLOW
}