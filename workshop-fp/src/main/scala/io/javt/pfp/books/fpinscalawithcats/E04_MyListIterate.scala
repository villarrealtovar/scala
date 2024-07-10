package io.javt.pfp.books.fpinscalawithcats

// Exercise: Iterate
class E04_MyListIterate {

  // Implement iterate using the same reasoning as we did for fill. This is slightly more complex than fill as we
  // need to keep two bits of information: the value of the counter and the value of type A.

  sealed abstract class MyList[A] extends Product with Serializable {
    def isEmpty: Boolean =
      this match {
        case Empty() => true
        case _ => false
      }

    def head: A = this match {
      case Pair(h, _) => h
    }

    def tail: MyList[A] = this match {
      case Pair(_, t) => t
    }
  }

  final case class Empty[A]() extends MyList[A]
  final case class Pair[A](_head: A, _tail: MyList[A]) extends MyList[A]

  object MyList {
    def unfold[A, B](seed: A)(stop: A => Boolean, f: A => B, next: A => A): MyList[B] =
      if (stop(seed)) Empty()
      else Pair(f(seed), unfold(next(seed))(stop, f, next))

    def fill[A](n: Int)(elem: => A): MyList[A] =
      unfold(n)(_ == 0, _ => elem, _ - 1)

    def iterate[A](start: A, len: Int)(f: A => A): MyList[A] =
      unfold((len, start))(
        a => a._1 == 0,
        a => a._2,
        a => (a._1 - 1, f(a._2))
      )
  }





}
