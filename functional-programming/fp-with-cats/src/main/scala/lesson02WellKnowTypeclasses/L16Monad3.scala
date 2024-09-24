package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._

object L16Monad3 extends App {

  implicit def eitherMonad[E]: Monad[Either[E, *]] =
    new Monad[Either[E, *]] {

      def flatMap[A, B](fa: Either[E, A])(
          f: A => Either[E, B]
      ): Either[E, B] = fa match {
        case Left(e)  => Left(e)
        case Right(a) => f(a)
      }

      def tailRecM[A, B](a: A)(
          f: A => Either[E, Either[A, B]]
      ): Either[E, B] = ???

      def pure[A](x: A): Either[E, A] = Right(x)

    }

  println(5.asRight[String]) // Right(5): Either[String, Int]
  println(5.asRight[String].flatMap(i => (i + 1).asRight[String])) // Right(6)
  println(
    5.asRight[String].flatMap(_ => "boom!".asLeft[Int])
  ) // Left("boom!"): Either[String, Int]

  println(
    5.asRight[String]
      .flatMap(_ =>
        "boom!".asLeft[Int].flatMap(_ => "boom 2!".asLeft[Int])
      ) // Left("boom!"): Either[String, Int]
  )
}
