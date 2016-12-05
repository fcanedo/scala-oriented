package oriented

/**
  * OrientFormat typeclass makes it able to transform from and to OrientElements from a specific model A.
  */
trait OrientFormat[A] {
  def name: String
  def encode(value: A): String
  def decode(json: String): Option[A]
}
