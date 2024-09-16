package lesson01Typeclasses

import scala.util.Using
import java.io.FileOutputStream
import scala.util.Try

object L07ChannelRead extends App {
  
  trait ByteDecoder[A] {
    def decode(bytes: Array[Byte]): Option[A]
  }

  object ByteDecoder{

    def apply[A](implicit ev: ByteDecoder[A]): ByteDecoder[A] = ev

    def instance[A](f: Array[Byte] => Option[A]): ByteDecoder[A] = new ByteDecoder[A] {
      override def decode(bytes: Array[Byte]): Option[A] =  f(bytes)
    }
  }

  implicit object StringByteDecoder extends ByteDecoder[String] {
    override def decode(bytes: Array[Byte]): Option[String] = Try(new String(bytes)).toOption
  }


  val a: Array[Byte] = Array(98, 105, 101, 110, 32, 58, 41)
  println(StringByteDecoder.decode(a))
  println(ByteDecoder[String].decode(a))

  
}

