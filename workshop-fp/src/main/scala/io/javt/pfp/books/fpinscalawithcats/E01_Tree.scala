package io.javt.pfp.books.fpinscalawithcats

// 2.2.4: Exercise: Tree
class E01_Tree {
  // To gain a bit of practice defining algebraic data types, code the following description in Scala (your choice of version, or do both.)
  //
  // A Tree with elements of type A is:
  //
  // - a Leaf with a value of type A; or
  // - a Node with a left and right child, which are both Trees with elements of type A.

  //My solution
  sealed abstract class Tree[A] extends Product with Serializable
  final case class Leaf[A](value: A) extends Tree
  final case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]



}
