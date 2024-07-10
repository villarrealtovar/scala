package io.javt.pfp.books.fpinscalawithcats

// 3.3. Exercise: Stream Combinators
class E06_Streams {

  // It’s time for you to get some practice with structural recursion and structural corecursion using codata.
  // Implement filter, zip, and scanLeft on Stream. They have the same semantics as the same methods on List,
  // and the signatures shown below.


  trait Stream[A] {
    def head: A
    def tail: Stream[A]

    def filter(pred: A => Boolean): Stream[A] = {
      val self = this
      new Stream[A] {
        def head: A = {
          def loop(stream: Stream[A]): A =
            if (pred(stream.head)) stream.head
            else loop(stream.tail)

          loop(self)
        }

        def tail: Stream[A] = {
          def loop(stream: Stream[A]): Stream[A] =
            if (pred(stream.head)) stream.tail
            else loop(stream.tail)

          loop(self)
        }
      }
    }

    def zip[B](that: Stream[B]): Stream[(A, B)] = {
      val self = this
      new Stream[(A, B)] {
        def head: (A, B) = (self.head, that.head)
        def tail: Stream[(A, B)] = self.tail.zip(that.tail)
      }
    }

    def scanLeft[B](zero: B)(f: (B, A) => B): Stream[B] = {
      val self = this
      new Stream[B] {
        def head: B = f(zero, self.head)
        def tail: Stream[B] = self.tail.scanLeft(this.head)(f)
      }
    }
  }

}
