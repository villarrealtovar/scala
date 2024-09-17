package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._

object L12Show extends App {

  case class Account(id: Long, number: String, balance: Double, owner: String)

  object Account {
    implicit val toStringShow: Show[Account] = Show.fromToString

    object Instances {
      implicit val byOwnerAndBalance: Show[Account] = Show.show { account =>
        s"${account.owner} - $$${account.balance}"
      }

      /** Exercise Write an instance of show which will output something like
        * 'this account belongs to Andres'
        */
      implicit val prettyByOwner: Show[Account] = Show.show { account =>
        s"this account belongs to ${account.owner}"
      }
    }

  }

  val account = Account(1, "123-56", 1000, "Andres")
  println(Account.toStringShow.show(account))
  println(Account.Instances.byOwnerAndBalance.show(account))
  println(Account.Instances.prettyByOwner.show(account))

  println(account.show)

  import Account.Instances.byOwnerAndBalance

  println(account.show)
}
