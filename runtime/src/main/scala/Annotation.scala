package java.lang.annotation


trait Annotation {
  def annotationType(): Class[Annotation]
}