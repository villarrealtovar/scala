package lesson02WellKnowTypeclasses

import cats._
import scala.util.Try

object L14Functor2 extends App {

  import cats.instances.list._ // includes Functor[List]
  println(Functor[List].map(List(1, 2, 3))(_ + 1))

  import cats.instances.option._ // includes Functor[Option]
  println(Functor[Option].map(Option(2))(_ + 1))

  import cats.instances.try_._
  println(Functor[Try].map(Try(42))(_ + 1))

  // Generaling an API
  def do10xList(list: List[Int]): List[Int] = list.map(_ * 10)
  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ * 10)
  def do10xTry(attempt: Try[Int]): Try[Int] = attempt.map(_ * 10)

  def do10x[F[_]](fa: F[Int])(implicit f: Functor[F]): F[Int] =
    f.map(fa)(_ * 10)

  println(do10x(List(1, 2, 3)))
  println(do10x(Option(3)))
  println(do10x(Try(42)))

  /** Exercise 1:
    *
    * define your own functor for a Binary tree
    *
    * hint: define an object which extends Functor[Tree]
    */
  trait Tree[+T]

  object Tree {
    def leaf[T](t: T): Tree[T] = Leaf(t)
    def branch[T](t: T, l: Tree[T], r: Tree[T]): Tree[T] = Branch(t, l, r)
  }

  case class Leaf[+T](value: T) extends Tree[T]
  case class Branch[+T](value: T, left: Tree[T], right: Tree[T]) extends Tree[T]

  implicit object TreeFunctor extends Functor[Tree] {
    override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = {
      fa match {
        case Leaf(a) => Leaf(f(a))
        case Branch(a, l, r) =>
          Branch(f(a), map(l)(f), map(r)(f))
      }
    }
  }

  println(
    do10x[Tree](Branch(2, Leaf(2), Branch(3, Leaf(4), Leaf(10))))
  ) // without smart constructors, you have to help compiler with do10x[Tree]

  println(
    do10x(
      Tree.branch(
        2,
        Tree.leaf(2),
        Tree.branch(3, Tree.leaf(4), Tree.leaf(10))
      ) // using smart constructor
    )
  )

  import cats.implicits._

  val tree: Tree[Int] =
    Tree.branch(42, Tree.branch(6, Tree.leaf(10), Tree.leaf(30)), Tree.leaf(20))
  val incrementedTree = tree.map(_ + 1)

  println(incrementedTree)

  /** Exercise 2
    *
    * Write a shorted do10x method using extension methods
    */

  def do10xShorted[F[_]: Functor](fa: F[Int]): F[Int] = fa.map(_ * 10)
  println(do10x(incrementedTree))
}
