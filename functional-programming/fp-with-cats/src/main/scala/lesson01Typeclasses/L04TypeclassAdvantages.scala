package lesson01Typeclasses

import scala.util.Using
import java.io.FileOutputStream
import java.nio.ByteBuffer

object L04TypeclassAdvantages extends App {
  
  trait ByteEncoder[A] {
    def encode(a: A): Array[Byte]
  }

  object ByteEncoder {

    implicit object StringByteEncoder extends ByteEncoder[String] {
      override def encode(a: String): Array[Byte] = a.getBytes()
    }
  }


  trait Channel {
    def write[A](obj: A)(implicit enc: ByteEncoder[A]): Unit
  }

  object FileChannel extends Channel {
    override def write[A](obj: A)(implicit enc: ByteEncoder[A]): Unit = {
      val bytes: Array[Byte] = enc.encode(obj)

      Using(new FileOutputStream("test")) { outputStream =>
        outputStream.write(bytes)
        outputStream.flush()
      }
    }
  }
   
 
  implicit object Rot3StringByteEncoder extends ByteEncoder[String] {
    override def encode(a: String): Array[Byte] = a.getBytes.map(b => (b + 3).toByte)
  }

  FileChannel.write[String]("little")

  /**
    * Common situtation:
    * For each of type A:
    *   - 1 main instance of ByteEncoder
    *   - couple more instances for specific uses
    */

   // Goal
   // - Use the main instance by default
   // - Provide a different instance for specific use cases

}
