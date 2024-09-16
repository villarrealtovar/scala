package lesson01Typeclasses

import scala.util.Using
import java.io.FileOutputStream

object L02ChannelInheritance extends App {
  
  trait ByteEncodable {
    def encode(): Array[Byte]
  }

  trait Channel {
    def write(obj: ByteEncodable): Unit 
  }

  case class FullName(firstName: String, lastName: String) extends ByteEncodable {
    override def encode(): Array[Byte] = {
      firstName.getBytes ++ lastName.getBytes 
    }
  }

  object FileChannel extends Channel {

    override def write(obj: ByteEncodable): Unit = {
      val bytes: Array[Byte] = obj.encode()

      Using(new FileOutputStream("./test")){ outputStream =>
        outputStream.write(bytes)
        outputStream.flush()
      }
    }
  }

  FileChannel.write(FullName("Andres", "Villarreal"))
}
