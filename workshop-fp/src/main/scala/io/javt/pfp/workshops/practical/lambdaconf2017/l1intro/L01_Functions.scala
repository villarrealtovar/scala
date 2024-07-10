package io.javt.pfp.workshops.practical.lambdaconf2017.l1intro

import cats.implicits._

object L01_Functions {

  /** Defining a function */

  val func: String => Int = (s: String) => s.length
  //TODO: val func_alt: String => Int = ???
  val func_alt: String => Int = _.length

  val func2: (Int, Int) => Int = (i1: Int, i2: Int) => i1 + i2
  //TODO: val func2_alt: (Int, Int) => Int = ???
  val func2_alt: (Int, Int) => Int = _ + _

  /** Using functions */
  val list = List("hello", "world", "!")
  list.map(func)
  list.map(func_alt)
  list.map(_.length)
  
  /** Function composition */

  // f(x) = x + 1
  val f = (x: Int) => x + 1
  // g(x) = "x"
  val g = (x: Int) => x.toString

  // h(x) = g(f(x))
  val h: Int => String = g compose f

  //TODO: val h_alt: Int => String = ???
  val h_alt: Int => String = f andThen g

  // operators from Cats
  val hz = g <<< f
  val hz_alt = f >>> g

  /** Higher Ordered Functions */
  val hof1: String => (Int => Int) = (str: String) => ((i: Int) => i + str.length)
  val hof2: (String => Int) => Int = (f: String => Int) => f("blablalbal")

  val hof1Val: Int => Int = hof1("hello")
  val hof2Val: Int = hof2(_.length)

  /** Curry */
  val threeArgs: (Int, String, Double) => Long =
    (i, s, d) => i.toLong + s.length.toLong + d.toLong
  //TODO: val curried: Int => String => Double => Long = ???
  val curried: Int => String => Double => Long = threeArgs.curried
  val oneArgApplied: String => Double => Long = curried(10)

  /** Partial application */
  val pa1: (String, Double) => Long = threeArgs(10, _:String, _:Double)
  val pa2: (Int, Double) => Long = threeArgs(_:Int, "hi", _:Double)

  /** Functions vs methods */
  class Foo(val str: String) {
    val func: Int => String = _.toString

    def method(i: Int): String = i.toString
  }

  val foo = new Foo("hello")
  foo.func(10)
  foo.method(10)

  /** do they compose */
  val temp: String => Int = _.toInt // I know, this function throws an error if the String is different a number,
                                    // but it's only for example

  val a: String => String = temp >>> foo.func // I could write this, because both are values
  val a1: String => String = temp >>> foo.method // This compiles because the compiler helps to developers, putting an
  // underscore at the end in the following way:
  // val a2: String => String = temp >>> foo.method _
  val a2: String => String = temp >>> foo.method _
  val a3: Int => String  = foo.method _
  // the compiler de-sugar the `a3` in the following code:
  val a4: Int => String = (i: Int) => foo.method(i)

  val b: Int => Int = foo.func >>> temp
  // val b1: Int => Int = foo.method >>> temp //ERROR: it throws an error because `methods` aren't objects with
  // `compose` and `andThen` methods, but `functions` are objects with `compose` and `andThen` methods.
  // The following code compiles:
  // val b2: Int => Int = (foo.method _) >>> temp
  // if we partially applied the method and the compiler converts the method to a function
  val b2: Int => Int = (foo.method _) >>> temp


  /** methods can be polymorphic */
  def bar[A, B, C](a: A, b: B, make: (A, B) => C): C =
    make(a, b)

  bar[Int, Int, Int](10, 20, _ + _)
  bar[String, Int, Double]("hello", 20, (s, i) => (s.length + i).toDouble)

  /** higher kinded types */
  def buz[F[_], A](f: F[A], tr: F[A] => A): A = tr(f)
  val buzres1: Int = buz[Option, Int](10.some, _.get)
  val buzres2: String = buz[Option, String]("hello".some, _.get)

  val t1: Some[Int] = Some(2)

  val t2: Option[Int] = 2.some

  val temp2: Either[String, Int] = ???
  buz[Either[String, *], Int](temp2, _)

  type Error[A] = Either[String, A]
  buz[Error, Int](temp2, _)
}
