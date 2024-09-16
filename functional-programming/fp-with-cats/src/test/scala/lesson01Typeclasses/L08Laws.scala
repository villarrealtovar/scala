package lesson01Typeclasses

import org.typelevel.discipline.Laws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll
import java.nio.ByteBuffer
import org.typelevel.discipline.scalatest.FunSuiteDiscipline
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.Configuration
import scala.util.Try

 
  object Tests {

    trait ByteDecoder[A]{
      def decode(bytes: Array[Byte]): Option[A]
    }  

    trait ByteEncoder[A]{
      def encode(a: A): Array[Byte]
    } 


    trait ByteCodec[A] extends ByteDecoder[A] with ByteEncoder[A]

    trait ByteCodecLaws[A] {
      def codec: ByteCodec[A]

      def isomorphism(a: A): Boolean = codec.decode(codec.encode(a)) == Some(a)
    }

    trait ByteCodecTests[A] extends Laws {
      def laws: ByteCodecLaws[A]

      def byteCodec(implicit arb: Arbitrary[A]): RuleSet = new DefaultRuleSet(
        name = "byteCodec",
        parent = None,
        "isomorphism" -> forAll(laws.isomorphism _) 
      ) 
    }

    object ByteCodecTest { 
      def apply[A](implicit ev: ByteCodec[A]): ByteCodecTests[A] = new ByteCodecTests[A] {
        override def laws: ByteCodecLaws[A] = new ByteCodecLaws[A] {
          override def codec: ByteCodec[A] = ev }
      }
    }
    

    implicit object IntByteCodec extends ByteCodec[Int] {
      override def encode(a: Int): Array[Byte] = {
        val bb = ByteBuffer.allocate(4)
        bb.putInt(a)
        bb.array()
      }

      override def decode(bytes: Array[Byte]): Option[Int] = {
        if (bytes.length != 4) None 
        else {
          val bb = ByteBuffer.allocate(4)
          bb.put(bytes)
          bb.flip()
          Some(bb.getInt())
        }
      }
    }

    object IntByteCodecLaws extends ByteCodecLaws[Int] {
      override def codec: ByteCodec[Int] = IntByteCodec
    }


    object IntByteCodecTest extends ByteCodecTests[Int]{
      override def laws: ByteCodecLaws[Int] = IntByteCodecLaws
    }

    implicit object StringByteCodec extends ByteCodec[String] {
      override def encode(a: String): Array[Byte] = a.getBytes 

      override def decode(bytes: Array[Byte]): Option[String] = Try(new String(bytes)).toOption 
    }

    object StringByteCodecLaws extends ByteCodecLaws[String] {
      override def codec: ByteCodec[String] = StringByteCodec
    }

    object StringByteCodecTests extends ByteCodecTests[String] {
      override def laws: ByteCodecLaws[String] = StringByteCodecLaws
    }

  }


  import Tests._


  class ByteCodecSpec extends AnyFunSuite with Configuration with FunSuiteDiscipline {
    checkAll("ByteCodec[Int]", IntByteCodecTest.byteCodec)
    checkAll("ByteCodec[Int]-2", ByteCodecTest[Int].byteCodec)
    checkAll("ByteCodec[String]", StringByteCodecTests.byteCodec)
    checkAll("ByteCodec[String]-2", ByteCodecTest[String].byteCodec)
  }

