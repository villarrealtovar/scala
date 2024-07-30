package io.javt.pfp.workshops.practical.lambdaconf2017.l2typeclasses

object E01_Exercise {
 
  /** Typeclasses */
  // 1. Implement Equalz[A] typeclass that has method def eq(a1: A, a2: A): Boolean

  // 2. Write EqualzOps & Equalz companion object with apply method

  // 3. Write instance for String and User
  case class User(login: String)

  // 4. if A & B have instance for Equalz, can u write generic instance for Tuple (A, B)

  // Solution
  trait Equalz[A] {
    def eq(a1: A, a2: A): Boolean
  }

  object Equalz {
    def apply[A: Equalz]: Equalz[A] = implicitly[Equalz[A]]

    implicit class EqualzOps[A: Equalz](a1: A) {
      def eq2(a2: A): Boolean = Equalz[A].eq(a1, a2)
    }

    implicit val StringEqualz: Equalz[String] = new Equalz[String] {
      def eq(a1: String, a2: String): Boolean = a1 == a2
    }

    implicit def TupledEqualz[A: Equalz, B: Equalz]: Equalz[(A, B)] = new Equalz[(A, B)] {
      def eq(a1: (A, B), a2: (A, B)): Boolean = a1._1.eq2(a2._1) && a1._2.eq2(a2._2)
    }

  }

  def main(args: Array[String]): Unit = {
    implicit val UserEqualz: Equalz[User] = new Equalz[User] {
      // We can implement the eq method in the following way, but we can re-use
      //
      // def eq(user1: User, user2: User): Boolean = user1.login == user2.login
      //
      // but, we can reuse the implicit value for Equalz[String]
      import Equalz._
      def eq(user1: User, user2: User): Boolean = user1.login.eq2(user2.login)
    }

    val weird1: (String, User) = ("Hola Mundo", User("Andres"))
    val weird2: (String, User) = ("Hola Mundo", User("Andres"))
    val weird3: (String, User) = ("Hello World", User("Andres"))

    import Equalz._
    println("=====>" + weird1.eq2(weird2))
    println("=====>" + weird1.eq2(weird3))

  }

}
