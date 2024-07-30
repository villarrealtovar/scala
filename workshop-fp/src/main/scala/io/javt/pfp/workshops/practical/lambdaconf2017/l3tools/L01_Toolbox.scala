package io.javt.pfp.workshops.practical.lambdaconf2017.l3tools

import cats._
import cats.effect.IO
import cats.syntax.all._
import cats.implicits._

object L01_Toolbox {
  val length: String => Int = _.length

  def lengthOperation1(str: String):Int = length(str)
  def lengthOperation2(str: Option[String]): Option[Int] = str.map(length)
  def lengthOperation3(str: Either[Throwable, String]): Either[Throwable, Int] = str.map(length)
  def lengthOperation4(str: IO[String]): IO[Int] = str.map(length)

  def lengthOperation5[F[_]: Functor](str: F[String]): F[Int] = Functor[F].map(str)(length)
  def lengthOperation6[F[_]: Functor](str: F[String]): F[Int] = str.map(length)

  val plus: (Int, Int) => Int = _ + _

  def plusOperation1[F[_]: Apply](f1: F[Int], f2: F[Int]): F[Int] = {
    val temp: F[Int => Int] = f1.map(plus.curried)
    Apply[F].ap(temp)(f2) // it's the same that: f2 <*> f1
  }

  def plusOperation2[F[_]: Apply](f1: F[Int], f2: F[Int]): F[Int] = {
    val temp: F[Int => Int] = f1.map(plus.curried)
    Apply[F].ap(temp)(f2) // it's the same that: f2 <*> f1
    // Apply[F].ap2(plus)(f1, f2) // ScalaZ has applicativeBuilder for using |@|. What is the equivalent in cats?
  }

  def plusOperationOneParameterNoF_1[F[_]: Applicative](f1: F[Int], f2: Int): F[Int] = {
    val temp: F[Int => Int] = f1.map(plus.curried)
    Apply[F].ap(temp)(f2.pure[F])
                    // ^^^^^^^ here, we lift eh f2 into F using applicative's pure method
  }

  def plusOperation3[F[_]: FlatMap](seed: F[String], f1: F[Int], f2: String => F[Int]): F[Int] = {
    val temp: F[Int => Int] = f1.map(plus.curried)
    Apply[F].ap(temp)(FlatMap[F].flatMap(seed)(f2))
    // Apply[F].ap(temp)(seed >>= f2) // the operator >>= is an alias for FlatMap[F].flatMap
  }

  def plusOperation4[F[_]: FlatMap](seed: F[String], f1: F[Int], f2: String => F[Int]): F[Int] = {
    for {
      s <- seed
      a <- f1
      b <- f2(s)
    } yield plus(a, b)
  }


  def sum1[A: Semigroup](a1: A, a2: A): A = a1 |+| a2

  def sum2[A: Monoid](list: List[A]): A = list.fold(Monoid[A].empty)(_ |+| _)

  def sum3[F[_]: Foldable, A: Monoid](f: F[A]): A = f.fold


  // exercises 1
  val problem1: IO[IO[Int]] = IO(IO(42))

  // val solution1: IO[Int] = ???
  val solution1: IO[Int] = problem1.flatten


  // exercise 2
  val problem2: List[IO[Int]] = List(IO(10), IO(20))

  val solution2: IO[List[Int]] = problem2.sequence

  // exercise 3
  class User(id: Int, login: String)
  val ids = List(1,2,3)
  val fetchUser: Int => IO[User] = (id: Int) => IO(new User(id, s"login $id"))
  val users: IO[List[User]] = ids.traverse(fetchUser)

  def main(args: Array[String]): Unit = {

    // Functor
    val str = "hello"
    lengthOperation1(str)

    val maybeString: Option[String] = str.some
    val strError: Either[Throwable, String] = str.asRight
    val fetchStr: IO[String] = IO(str)

    lengthOperation6[Option](maybeString)
    lengthOperation6[IO](fetchStr)

    type Error[A] = Either[Throwable, A]
    lengthOperation6[Error](strError)
    lengthOperation6[Either[Throwable, *]](strError)

    type Id[A] = A
    lengthOperation6[Id](str)


    // Apply
    val l1 = 10
    val l2 = 20
    println(plusOperation1[Option](l1.some, l2.some))

    //Applicative
    println(plusOperationOneParameterNoF_1[Option](l1.some, l2))

    // FlatMap
    println(plusOperation3[Option](Some("hello"), l1.some, (s: String) => Some(s.length)))

    // Foldable
    println(sum3(List(1,2,3)))
    println(sum3(List("a", "b")))

  }
}