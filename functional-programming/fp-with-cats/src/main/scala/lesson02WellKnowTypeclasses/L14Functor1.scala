package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import scala.collection.immutable

object L14Functor1 extends App {

  class Secret[A](val value: A) {
    private def hashed: String = {
      val s = value.toString
      val bytes = s.getBytes(StandardCharsets.UTF_8)
      val d = MessageDigest.getInstance("SHA-1")
      val hashBytes = d.digest(bytes)
      new String(hashBytes, StandardCharsets.UTF_8)
    }

    override def toString: String = hashed
  }

  object Secret {
    implicit val secretFunctor: Functor[Secret] = new Functor[Secret] {
      override def map[A, B](fa: Secret[A])(f: A => B): Secret[B] = new Secret(
        f(fa.value)
      )
    }
  }

  val andresSecret = new Secret("Andres")
  println(andresSecret)
  println(andresSecret.value)

  val upperAndresSecret: Secret[String] =
    Functor[Secret].map(andresSecret)(_.toUpperCase)
  println(upperAndresSecret)
  println(upperAndresSecret.value)

  /** Exercise 1
    *
    * val optionFunctor: Functor[Option] = ???
    */
  val optionFunctor: Functor[Option] = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case None    => None
      case Some(a) => Some(f(a))
    }
  }

  println(optionFunctor.map(Some(3))(_ + 1))
  println(optionFunctor.as(Some(4), "hello world!"))

  /** Exercise 2
    *
    * val listFunctor: Functor[List] = ???
    */
  val listFunctor: Functor[List] = new Functor[List] {
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa match {
      case x :: xs => f(x) :: map(xs)(f)
      case Nil     => Nil
    }
  }

  println(listFunctor.map(List(1, 2, 3))(_ * 2))
  println(listFunctor.as(List(1, 2, 3, 4), 10))
}
