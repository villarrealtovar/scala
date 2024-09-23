package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._
import scala.collection.immutable

object L15Applicative4 extends App {

  val optionApplicative: Applicative[Option] = new Applicative[Option] {
    override def pure[A](x: A): Option[A] = Some(x)

    override def ap[A, B](ff: Option[A => B])(fa: Option[A]): Option[B] =
      (ff, fa) match {
        case (Some(f), Some(a)) => Some(f(a))
        case _                  => None
      }
  }

  println(optionApplicative.map2(Some(3), Some(4))(_ + _))
  println(optionApplicative.map2[Int, Int, Int](None, Some(4))(_ + _))

  val listApplicative: Applicative[List] = new Applicative[List] {
    override def ap[A, B](ff: List[A => B])(fa: List[A]): List[B] =
      (ff, fa) match {
        // The following code seems to work, but it uses a `for comprehension`,
        // but the f1 it's not necessary in the next call
        // case (f, as) =>
        //   for {
        //     f1 <- f
        //     a <- as
        //   } yield f1(a)
        case (f :: fs, a :: as) =>
          (a :: as).fmap(f) ++ ap(fs)(a :: as)
        case _ => Nil
      }

    override def pure[A](x: A): List[A] = List(x)

  }

  def sum1(a: Int): Int = a + 1
  def mul1(a: Int): Int = a * 3

  println(listApplicative.ap(List(sum1 _, mul1 _))(List(1, 3, 4)))

  println(listApplicative.map2(List(1, 2, 3), List(4, 5))(_ + _))
  println(listApplicative.map2(List[Int](), List(4, 5))(_ + _))
  println(listApplicative.map2(List[Int](1, 2, 3), List[Int]())(_ + _))
  // println(listApplicative.map2(List[Int](1, 2, 3), List(4, 5)) {}) // You cannot send a "empty"/"none" function to map2

}
