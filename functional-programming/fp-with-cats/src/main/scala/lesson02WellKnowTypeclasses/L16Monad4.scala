package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._
import scala.util._

object L16Monad4 extends App {

  implicit val tryMonad: Monad[Try] = new Monad[Try] {
    override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa match {
      case Failure(e) => Failure(e)
      case Success(a) => f(a)
    }

    override def tailRecM[A, B](a: A)(f: A => Try[Either[A, B]]): Try[B] = ???

    override def pure[A](x: A): Try[A] = Success(x)

  }

  println(tryMonad.pure(5))
  println(tryMonad.pure(5).flatMap(i => tryMonad.pure(i + 1)))

  println(tryMonad.pure(5).flatMap(_ => Failure(new Exception("boom"))))
  println(
    tryMonad
      .pure(5)
      .flatMap(_ => Failure(new Exception("boom")))
      .flatMap(j => Failure(new Exception("boom 2")))
  )

  // why Try is not a complete/real Monad
  Success(5).flatMap(i => throw new Exception("boom"))

  // Remember the law:
  // pure(x).flatMap(f) === f(x)

  // correct example of the law
  val f: Int => Try[Int] = i => Success(i + 1)
  println(Success(42).flatMap(f) == f(42))

  val f2: Int => Try[Int] = i => throw new Exception("boom")
  println(Success(42).flatMap(f2))
  println(f2(42)) // the law was broken f2(42) != Success(42).flatMap(f2)

}
