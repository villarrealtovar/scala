package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._

object L10Eq extends App {

  case class Account(id: Long, number: String, balance: Double, owner: String)

  object Account {
    implicit val universalEq: Eq[Account] = Eq.fromUniversalEquals // ==

    object Instances {
      // implicit val byIdEq: Eq[Account] = Eq.instance[Account]((a1, a2) =>  a1.id == a2.id)
      // implicit val byIdEq: Eq[Account] = Eq.instance[Account]((a1, a2) => Eq[Long].eqv(a1.id,a2.id))
      implicit def byIdEq(implicit eqLong: Eq[Long]): Eq[Account] =
        Eq.instance[Account]((a1, a2) => eqLong.eqv(a1.id, a2.id))

      // implicit def byIdEq2(implicit eqLong: Eq[Long]): Eq[Account] = Eq.by(account => account.id)
      implicit def byIdEq2(implicit eqLong: Eq[Long]): Eq[Account] = Eq.by(_.id)

      /** Exercise
        *
        * compare two accounts by number
        */
      implicit def byNumber(implicit eqString: Eq[String]): Eq[Account] =
        Eq.by(_.number)
    }
  }

  val account1 = Account(1, "123-56", 1000, "Andres Villa")
  val account2 = Account(2, "123-56", 1500, "Carolina Paz")

  println(Eq[Account].eqv(account1, account2))

  println(Account.Instances.byIdEq.eqv(account1, account2))

  println(Account.Instances.byNumber.eqv(account1, account2))

  println(account1 === account2) // use universalEq instance

  import Account.Instances.byNumber
  println(account1 === account2) // use byNumber instance

  // I can import a Instances and storage in a `val`
  // implicit val eqToUser: Eq[Account] = Account.Instances.byIdEq2

}
