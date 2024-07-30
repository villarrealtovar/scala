package io.javt.pfp.workshops.practical.lambdaconf2017.l1intro

object L04_Exercise {

  /** Functions */
  // 1. write a function weirdo that takes Int and String as parameters and adds Int to length of the String
  // val weirdo: ??? = ???
  val weirdo: Int => String => Int = n => s => n + s.length

  // 2. implement above but as a method
  // def weirdo(???): ??? = ???
  def weirdo(n: Int, s: String): Int = n + s.length

  // 3. bar = all elements in foo whose length > 4
  val foo = List("hello", "world", "!")
  // val bar = ???
  val bar: List[String] = foo.filter(_.length > 4)

  // 4. write a method tupled that
  // - takes a function f as argument
  // - function f takes two parameters (a: A, b: B) and returns type C
  // - method tupled should return a new function that takes tuple t: (A, B) as parameter and returns  value of type C
  // def tupled[A, B, C](???): ??? = ???
  def tupled[A, B, C](f: (A, B) => C): (A, B) => C = f

  // 5. implement andThen method
  // usage example
  // val len: String => Int = _.length
  // val plus10: Int => Double = _.toDouble + 10
  // val result: String => Double = andThen(len, plus10)

  def andThen[A, B, C](f: A => B, g: B => C): A => C = g compose f

  /** Pattern matching & Scalaz types */
  // 1. write method extract that returns third element of the list or error if does not exist
  // def extract(list: List[Int]): ??? = ???
  def extract(list: List[Int]): Either[Throwable, Int] = {
    list match {
      case xs if xs.length >= 3 => Right(xs(3))
      case _ => Left(new RuntimeException("3 third element no found"))
    }
  }




}
