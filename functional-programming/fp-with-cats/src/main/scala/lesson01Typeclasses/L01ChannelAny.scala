package lesson01Typeclasses

import java.io.FileOutputStream
import scala.util.Using
import java.nio.ByteBuffer

object L01ChannelAny extends App {


  trait Channel {
    def write(obj: Any): Unit 
  } 


  object FileChannel extends Channel {
    override def write(obj: Any): Unit = {
      val bytes: Array[Byte] = obj match {
        case n: Int =>
          val bb = ByteBuffer.allocate(4)
          bb.putInt(n)
          bb.array()
        case s: String =>
          s.getBytes()
      case invalid => throw new Exception("unhandled")
      }

  
      Using(new FileOutputStream("./test")){ outputStream =>
        outputStream.write(bytes)
        outputStream.flush()
      }
    }
  }

  FileChannel.write("hello")

}

