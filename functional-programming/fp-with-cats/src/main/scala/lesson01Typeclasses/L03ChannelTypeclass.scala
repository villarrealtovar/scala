package lesson01Typeclasses

import scala.util.Using
import java.io.FileOutputStream
import java.nio.ByteBuffer

object L03ChannelTypeclass extends App {
  
  trait ByteEncoder[A] {
    def encode(a: A): Array[Byte]
  }

  trait Channel {
    def write[A](obj: A, enc: ByteEncoder[A]): Unit
  }

  object FileChannel extends Channel {
    override def write[A](obj: A, enc: ByteEncoder[A]): Unit = {
      val bytes: Array[Byte] = enc.encode(obj)

      Using(new FileOutputStream("test")) { outputStream =>
        outputStream.write(bytes)
        outputStream.flush()
      }
    }
  }


  object IntByteEncoder extends ByteEncoder[Int] {
    override def encode(a: Int): Array[Byte] = {
      val bb = ByteBuffer.allocate(4)
      bb.putInt(a)
      bb.array()
    }
  }
  
  FileChannel.write[Int](42, IntByteEncoder)


  /**
    * Exercise: Write a Instance of ByteEncoder for String
    */

   object StringByteEncoder extends ByteEncoder[String] {
     override def encode(a: String): Array[Byte] = a.getBytes()
   }

   FileChannel.write[String]("hello", StringByteEncoder)
}
