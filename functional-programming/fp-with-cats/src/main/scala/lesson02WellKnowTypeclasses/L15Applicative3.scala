package lesson02WellKnowTypeclasses

import cats.effect.IO
import cats._
import cats.data._
import cats.implicits._

object L15Applicative3 extends App {

  /** 1. Sequencing commands */

  /** Example 1.1
    */

  // Using a Monad inside of sequence_v1
  def sequence_v1[A](ios: List[IO[A]]): IO[List[A]] =
    ios match {
      case Nil => IO.pure(List.empty[A])
      case x :: xs =>
        for {
          c <- x
          cs <- sequence_v1(xs)
        } yield (c +: cs)
    }

  // without using Monad/for comprehension
  def sequence_v2[A](ios: List[IO[A]]): IO[List[A]] =
    ios match {
      case Nil => IO.pure(List.empty[A])
      case x :: xs =>
        IO.pure((a: A) => (listA: List[A]) => a +: listA)
          .ap(x)
          .ap(sequence_v2(xs))

    }

  /** Example 1.1.2
    */
  def inc(n: Int): Int = n + 1

  val incremented = Applicative[Option].pure(a => inc(a)).ap(10.some)

  println(incremented)

  /** Example 1.3
    */
  def add(a: Int, b: Int): Int = a + b

  val added: Option[Int] =
    Applicative[Option].pure((a: Int) => b => add(a, b)).ap(10.some).ap(20.some)

  println(s"added $added")

  /** 2. Evaluation Expressions */

  /** Example 2.1
    */
  sealed trait Exp
  case class Val(value: Int) extends Exp
  case class Add(left: Exp, right: Exp) extends Exp
  case class Var(key: String) extends Exp

  case class Env[K](kv: Map[K, Int])

  def fetch(key: String)(env: Env[String]) = env.kv.getOrElse(key, 0)

  def eval(exp: Exp, env: Env[String]): Int = {
    exp match {
      case Val(value)       => value
      case Add(left, right) => eval(left, env) + eval(right, env)
      case Var(key)         => fetch(key)(env)
    }
  }

  val env1 = Env(Map("x" -> 3, "y" -> 10))
  val exp1 = Add(Val(10), Add(Var("x"), Var("y")))

  println(s"Eval : ${eval(exp1, env1)}")

  /** Example 2.2
    */
  type EnvReader[A] = Reader[Map[String, Int], A]

  def fetchR(key: String) =
    Reader[Map[String, Int], Int](env => env.getOrElse(key, 0))

  def evalR(exp: Exp): Reader[Map[String, Int], Int] = {
    exp match {
      case Val(value) => Applicative[EnvReader].pure(value)
      case Add(left, right) =>
        val f = Applicative[EnvReader].pure((a: Int) => (b: Int) => a + b)
        val l = evalR(left)
        val r = evalR(right)
        // f.ap(l).ap(r)
        Applicative[EnvReader].ap(Applicative[EnvReader].ap(f)(l))(r)
      case Var(key) => fetchR(key)
    }
  }

  /** 3. Transposing matrix ^ */

  /** Example 3.1 */
  val matrix = LazyList(
    LazyList(1, 2, 3, 4, 5),
    LazyList(6, 7, 8, 9, 10),
    LazyList(11, 12, 13, 14, 15)
  )

  val transposed_example = LazyList(
    LazyList(1, 6, 11),
    LazyList(2, 7, 12),
    LazyList(3, 8, 13),
    LazyList(4, 9, 14),
    LazyList(5, 10, 15)
  )

  def zipWith[A, B, C](as: LazyList[A], bs: LazyList[B])(
      f: (A, B) => C
  ): LazyList[C] =
    as.zip(bs).map { case (a, b) => f(a, b) }

  def repeat[A](a: A): LazyList[A] = a #:: repeat(a)

  def transpose_v1[A](matrix: LazyList[LazyList[A]]): LazyList[LazyList[A]] =
    matrix match {
      case LazyList() => repeat(LazyList.empty)
      case xs #:: xss =>
        zipWith(xs, transpose_v1(xss)) { case (a, as) =>
          a +: as
        }
    }

  /** Example 3.2 */

  def zapp[A, B](fs: LazyList[A => B])(as: LazyList[A]): LazyList[B] =
    fs.zip(as).map { case (f, a) => f(a) }

  def tranpose_v2[A, B](matrix: LazyList[LazyList[A]]): LazyList[LazyList[A]] =
    matrix match {
      case LazyList() => repeat(LazyList.empty)
      case xs #:: xss =>
        val fs = repeat((a: A) => (as: LazyList[A]) => a +: as)
        val zap1 = zapp(fs)(xs)
        zapp(zap1)(tranpose_v2(xss))
    }
}
