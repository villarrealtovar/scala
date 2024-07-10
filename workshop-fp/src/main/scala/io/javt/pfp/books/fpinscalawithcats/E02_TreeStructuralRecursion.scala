package io.javt.pfp.books.fpinscalawithcats

// 2.3: Exercise: Methods for Tree
class E02_TreeStructuralRecursion {

  // In a previous exercise we created a Tree algebraic data type:
  sealed abstract class Tree[A] extends Product with Serializable {
    //My solution using Pattern Matching
    def size: Int = {
      this match {
        case Leaf(_) => 1
        case Node(l, r) => l.size + r.size
      }
    }

    def contains(a: A): Boolean = {
      this match {
        case Leaf(v) => v == a
        case Node(l, r) => l.contains(a) || r.contains(a)
      }
    }

    def map[B](f: A => B): Tree[B] = {
      this match {
        case Leaf(v) => Leaf(f(v))
        case Node(l, r) => Node(l.map(f), r.map(f))
      }
    }
  }

  final case class Leaf[A](value: A) extends Tree[A]

  final case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]


  // Let’s get some practice with structural recursion and write some methods for Tree. Implement
  //
  // - size, which returns the number of values (Leafs) stored in the Tree;
  // - contains, which returns true if the Tree contains a given element of type A, and false otherwise; and
  // - map, which creates a Tree[B] given a function A => B
  //
  //Use whichever you prefer of pattern matching or dynamic dispatch to implement the methods.


}
