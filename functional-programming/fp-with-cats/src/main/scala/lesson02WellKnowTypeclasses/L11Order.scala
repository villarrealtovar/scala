package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._

object L11Order extends App {

  case class Account(id: Long, number: String, balance: Double, owner: String)

  object Account {
    // implicit val orderById: Order[Account] =
    //   Order.from((a1, a2) => Order[Long].compare(a1.id, a2.id))

    implicit def orderById(implicit orderLong: Order[Long]): Order[Account] =
      Order.from((a1, a2) => orderLong.compare(a1.id, a2.id))

    object Instances {

      implicit val orderByNumber: Order[Account] =
        Order.by(account => account.number)

      /** Exercise
        *
        * Provide an instance of Order[Account] that orders by balance
        */
      implicit val orderByBalance: Order[Account] = Order.by(_.balance)

      implicit def orderByBalance2(implicit
          orderDouble: Order[Double]
      ): Order[Account] = Order.by(_.balance)

      implicit def orderByBalance3: Order[Account] = Order.by(_.balance)
    }
  }

  def sort[A](list: List[A])(implicit order: Order[A]) = {
    list.sorted(order.toOrdering)
  }

  val account1 = Account(3, "442-21", 4000, "Alice")
  val account2 = Account(2, "442-21", 2400, "Bob")
  val account3 = Account(1, "123-56", 3401, "Charlie")

  val myList = List(account1, account2, account3)
  println(s"myList unsorted: $myList")
  println(s"myList sorted by id: ${sort[Account](myList)}")

  import Account.Instances.orderByBalance
  println(s"myList sorted by balance: ${sort[Account](myList)}")

  println(account1 compare account2)
  println(account1 min account2)
  println(account1 max account2)

  val orderByIdDesc: Order[Account] = Order.reverse(
    Account.orderById
  ) // this would be an `implicit`, but there're two in scope.
  println(
    s"myList sorted by reverse id: ${sort[Account](myList)(orderByIdDesc)}"
  )
}
