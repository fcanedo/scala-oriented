import io.circe.{Decoder, Encoder}
import io.circe.parser.parse
import oriented.OrientFormat

import scala.reflect.ClassTag

package object dsl {

  implicit def orientFormat[A : ClassTag : Encoder : Decoder] = new OrientFormat[A] {
    override def name: String = implicitly[ClassTag[A]].runtimeClass.getSimpleName

    override def encode(value: A): String = implicitly[Encoder[A]].apply(value).toString()

    override def decode(json: String): Option[A] =
      parse(json).right.flatMap(json => implicitly[Decoder[A]].decodeJson(json)).right.toOption
  }

}
