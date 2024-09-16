package lesson01Typeclasses

import scala.util.Using
import java.io.FileOutputStream
import java.nio.ByteBuffer

object L05HelperMethods extends App {
  
  trait ByteEncoder[A] {
    def encode(a: A): Array[Byte]
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


  case class Switch(isOn: Boolean)
  object Switch {

    implicit object SwitchByteEncoder extends ByteEncoder[Switch] {
      override def encode(a: Switch): Array[Byte] = Array(if (a.isOn) '1'.toByte else '0'.toByte)
    }
  }


  FileChannel.write[Switch](Switch(true))

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
