package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._
import scala.collection.immutable

object L16Monad2 extends App {

  val result = for {
    a <- List(1, 2, 3)
    b <- List(4, 5, 6)
  } yield a + b

  println(s"result: $result")

  val listMonad: Monad[List] = new Monad[List] {
    override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] =
      fa match {
        case a :: as => f(a) ++ flatMap(as)(f)
        case Nil     => Nil
      }

    override def tailRecM[A, B](a: A)(f: A => List[Either[A, B]]): List[B] = ???

    override def pure[A](x: A): List[A] = List(x)

  }

  println(listMonad.flatMap(List(1, 2, 3))(a => List(a + 1, a + 2)))
}
