package lesson01Typeclasses

import java.nio.ByteBuffer

object L08AutomaticDerivation extends App {

  trait ByteEncoder[A] {
    def encode(a: A): Array[Byte] 
  }

  object ByteEncoder {
    def apply[A](implicit ev: ByteEncoder[A]): ByteEncoder[A] = ev 
  }

  implicit object StringByteEncoder extends ByteEncoder[String] {
    override def encode(a: String): Array[Byte] = a.getBytes 
  }

  implicit object IntByteEncoder extends ByteEncoder[Int] {
    override def encode(a: Int): Array[Byte] = {
      val bb = ByteBuffer.allocate(4)
      bb.putInt(a)
      bb.array()
    }
  }


  // implicit object OptionString extends ByteEncoder[Option[String]] {
  //   def encode(a: Option[String]): Array[Byte] = a match {
  //     case None => Array[Byte]() 
  //     case Some(value) => StringByteEncoder.encode(value)
  //   }
  // }
  // 
  // implicit object OptionInt extends ByteEncoder[Option[Int]] {
  //   def encode(a: Option[Int]): Array[Byte] = a match {
  //     case None => Array[Byte]() 
  //     case Some(value) => IntByteEncoder.encode(value)
  //   }
  // }


  implicit def optionEncoder[A](implicit encA: ByteEncoder[A]): ByteEncoder[Option[A]] = new ByteEncoder[Option[A]] {
    override def encode(a: Option[A]): Array[Byte] = a match {
      case None => Array[Byte]()
      case Some(value) => encA.encode(value)
    }
  }


  println(ByteEncoder[String].encode("hello"))
  println(ByteEncoder[Int].encode(1000).toString)
  println(ByteEncoder[Option[String]].encode(Option("world")))
  println(ByteEncoder[Option[String]].encode(None))
  println(ByteEncoder[Option[Int]].encode(Option(100)))
  println(ByteEncoder[Option[Int]].encode(None))
  println(ByteEncoder[Option[Int]].encode(Some(100)))
}
