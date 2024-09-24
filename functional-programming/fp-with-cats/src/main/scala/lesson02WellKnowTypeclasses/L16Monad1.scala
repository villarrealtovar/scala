package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._

object L16Monad1 extends App {

  sealed trait MyOption[+A]

  object MyOption {
    case class MySome[+A](a: A) extends MyOption[A]
    case object MyNone extends MyOption[Nothing]

    implicit val monadMyOption: Monad[MyOption] = new Monad[MyOption] {
      def flatMap[A, B](fa: MyOption[A])(f: A => MyOption[B]): MyOption[B] =
        fa match {
          case MySome(a) => f(a)
          case MyNone    => MyNone
        }

      def tailRecM[A, B](a: A)(f: A => MyOption[Either[A, B]]): MyOption[B] =
        ???

      def pure[A](x: A): MyOption[A] = MySome(x)

      override def map[A, B](fa: MyOption[A])(f: A => B): MyOption[B] =
        flatMap(fa)(a => pure(f(a)))

      override def flatten[A](ffa: MyOption[MyOption[A]]): MyOption[A] =
        flatMap(ffa)(identity)

    }

  }

  val x: MyOption[Int] = Monad[MyOption].pure(42)
  println(s"x: $x")

  val y: MyOption[Int] =
    Monad[MyOption].pure(40).flatMap(i => Monad[MyOption].pure(i + 1))
  println(s"y: $y")

  import MyOption._
  val z: MyOption[Int] =
    (MyNone: MyOption[Int]).flatMap(i => Monad[MyOption].pure(i + 1))
  println(s"z: $z")

  val w: MyOption[Int] =
    (MyNone: MyOption[Int]).flatMap(_ => MyNone)
  println(s"w: $w")

  val t: MyOption[Int] = for {
    a <- Monad[MyOption].pure(42)
    b <- Monad[MyOption].pure(43)
  } yield a + b

  println(s"t: $t")

  val s: MyOption[Int] = for {
    a <- Monad[MyOption].pure(42)
    b <- (MyNone: MyOption[Int])
  } yield a + b
  println(s"s: $s")

  val ffa: MyOption[MyOption[Int]] =
    Monad[MyOption].pure(Monad[MyOption].pure(6))
  println(s"ffa: $ffa")

  println(ffa.flatten)
  println(Monad[MyOption].flatten(ffa))

}
