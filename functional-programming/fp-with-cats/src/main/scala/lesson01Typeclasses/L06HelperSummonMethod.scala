package lesson01Typeclasses

object L06HelperSummonMethod extends App {
  
  trait ByteEncoder[A] {
    def encode(a: A): Array[Byte]
  }

  object ByteEncoder {
    implicit object StringByteEncoder extends ByteEncoder[String] {
      override def encode(a: String): Array[Byte] = a.getBytes
    }

    def summon[A](implicit ev: ByteEncoder[A]): ByteEncoder[A] = ev // we can rename to `apply`
    def apply[A](implicit ev: ByteEncoder[A]): ByteEncoder[A] = ev 
  }


    implicit object Rot3StringByteEncoder extends ByteEncoder[String] {
      override def encode(a: String): Array[Byte] = a.getBytes.map(c => (c + 3).toByte)
    }


  println(ByteEncoder.StringByteEncoder.encode("hello")) // calling the method in this way, it's not flexible


  println(implicitly[ByteEncoder[String]].encode("hello"))

  println(ByteEncoder.summon[String].encode("hello"))
  println(ByteEncoder.apply[String].encode("hello"))
  println(ByteEncoder[String].encode("hello"))
}
