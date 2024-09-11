package lesson01Actors

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.ActorSelection
import lesson01Actors.L02ActorCapabilities.Bank

object L04ChildActors extends App {

  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }

  class Parent extends Actor {
    import Parent._

    def receive: Receive = { case CreateChild(name) =>
      println(s"${self.path} creating child")
      val childRef = context.actorOf(Props[Child], name)
      context.become(withChild(childRef))
    }

    def withChild(childRef: ActorRef): Receive = { case TellChild(message) =>
      childRef forward message
    }
  }

  class Child extends Actor {
    def receive: Receive = { case message =>
      println(s"${self.path} I got: $message")
    }
  }

  val system = ActorSystem("ParentChildDemo")

  import Parent._
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! CreateChild("child")

  parent ! TellChild("hey Kid!")

  // actor selection
  Thread.sleep(1000)
  val childSelection: ActorSelection =
    system.actorSelection("/user/parent/child")

  childSelection ! "I found you"

  /** DANGER!
    *
    * NEVER PASS MUTABLE ACTOR STATEM, OR THE `THIS` REFERENCE TO CHILD ACTORS
    */
  object NaiveBankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object InitializeAccount
  }
  class NaiveBankAccount extends Actor {
    import NaiveBankAccount._
    import CreditCard._

    var amount = 0
    def receive: Receive = {
      case InitializeAccount =>
        val creditCardRef = context.actorOf(Props[CreditCard], "card")
        creditCardRef ! AttachToAccount(this) // !!
      case Deposit(funds)  => deposit(funds)
      case Withdraw(funds) => widthdraw(funds)

    }

    def deposit(funds: Int) = {
      println(s"${self.path} depositing $funds on top of $amount")
      amount += funds
    }
    def widthdraw(funds: Int) = {
      println(s"${self.path} withdrawing $funds from $amount")
      amount -= funds
    }
  }

  object CreditCard {
    case class AttachToAccount(bankAccount: NaiveBankAccount) // !!
    case object CheckStatus
  }
  class CreditCard extends Actor {
    import CreditCard._

    def receive: Receive = { case AttachToAccount(account) =>
      context.become(attachedTo(account))
    }

    def attachedTo(account: NaiveBankAccount): Receive = { case CheckStatus =>
      println(s"${self.path} your message has been processed.")
      account.widthdraw(1) // because I can
    }
  }

  import NaiveBankAccount._
  import CreditCard._

  val bankAccountRef = system.actorOf(Props[NaiveBankAccount], "account")
  bankAccountRef ! InitializeAccount
  bankAccountRef ! Deposit(100)

  Thread.sleep(500)
  val creditCardSelection = system.actorSelection("/user/account/card")
  creditCardSelection ! CheckStatus // WRONG

}
