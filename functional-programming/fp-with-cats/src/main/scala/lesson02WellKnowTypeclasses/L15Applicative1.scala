package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._

object L15Applicative1 extends App {

  sealed trait Validated[+A]

  object Validated {
    case class Valid[+A](a: A) extends Validated[A]
    case class Invalid(errors: List[String]) extends Validated[Nothing]

    implicit val applicative: Applicative[Validated] =
      new Applicative[Validated] {
        override def ap[A, B](ff: Validated[A => B])(
            fa: Validated[A]
        ): Validated[B] = // map2(ff, fa)((f, a) => f(a))
          (ff, fa) match {
            case (Valid(f), Valid(a))       => Valid(f(a))
            case (Invalid(e), Valid(_))     => Invalid(e)
            case (Valid(_), Invalid(e))     => Invalid(e)
            case (Invalid(e1), Invalid(e2)) => Invalid(e1 ++ e2)
          }

        override def pure[A](x: A): Validated[A] = Valid(x)

        // override def map[A, B](fa: Validated[A])(f: A => B): Validated[B] = ???

        override def map2[A, B, C](fa: Validated[A], fb: Validated[B])(
            f: (A, B) => C
        ): Validated[C] = {
          // The expressin:  `val g: A => B => C = a => b => f(a, b)` is the same that
          // `val g: A => B => C = f.curried`
          ap(ap(pure(f.curried))(fa))(fb)
        }

        // (fa, fb) match {
        //   case (Valid(a), Valid(b))       => Valid(f(a, b))
        //   case (Invalid(e), Valid(_))     => Invalid(e)
        //   case (Valid(_), Invalid(e))     => Invalid(e)
        //   case (Invalid(e1), Invalid(e2)) => Invalid(e1 ++ e2)
        // }
        //
        //

        // override def map3[A, B, C, D](
        //     f0: Validated[A],
        //     f1: Validated[B],
        //     f2: Validated[C]
        // )(f: (A, B, C) => D): Validated[D] = ???
        //
        def tupled[A, B](
            va: Validated[A],
            vb: Validated[B]
        ): Validated[(A, B)] = map2(va, vb)((a, b) => (a, b))
      }

  }

  val v1: Validated[Int] = Applicative[Validated].pure(1)
  val v2: Validated[Int] = Applicative[Validated].pure(2)
  val v3: Validated[Int] = Applicative[Validated].pure(3)

  println((v1, v2, v3).mapN((a, b, c) => a + b + c))
  println((v1, v2).mapN((a, b) => a + b))
}
