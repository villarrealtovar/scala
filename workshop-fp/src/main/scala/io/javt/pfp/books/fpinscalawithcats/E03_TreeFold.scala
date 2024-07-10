package io.javt.pfp.books.fpinscalawithcats

// 2.3.5: Exercise: Tree Fold
// 2.3.5: Exercise: Using Fold
class E03_TreeFold {

  // Implement a fold for Tree defined earlier. There are several different ways to traverse a
  // tree (pre-order, post-order, and in-order). Just choose whichever seems easiest.

  sealed abstract class Tree[A] extends Product with Serializable {
    def fold[B](leaf: A => B)(node: (B, B) => B): B = {
      this match {
        case Leaf(v) => leaf(v)
        case Node(l, r) => node(l.fold(leaf)(node), r.fold(leaf)(node))
      }
    }


    // Exercise: Using Fold

    // Prove to yourself that you can replace structural recursion with calls to fold, by redefining size,
    // contains, and map for Tree using only fold.
    def size: Int = this.fold(_ => 1)(_ + _)

    def contains(a: A): Boolean = this.fold(_ == a)(_ || _)

    def map[B](f: A => B): Tree[B] = this.fold(v => Leaf(f(v)): Tree[B])((l, r) => Node(l, r): Tree[B])
  }

  final case class Leaf[A](value: A) extends Tree[A]

  final case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]



}
