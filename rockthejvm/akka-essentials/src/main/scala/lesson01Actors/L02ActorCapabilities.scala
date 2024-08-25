package lesson01Actors

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.ActorRef
import lesson01Actors.L02ActorCapabilities.Counter.Increment
import lesson01Actors.L02ActorCapabilities.Bank.Withdraw
import lesson01Actors.L02ActorCapabilities.Person.LiveTheLive

object L02ActorCapabilities extends App {

  class SimpleActor extends Actor {
    def receive: Receive = {
      case "Hi!" =>
        println(s"sending back ${sender()}")
        sender() ! "Hello there"
      case message: String =>
        println(s"[${context.self}]: I have receive $message")
      case number: Int =>
        println(s"[${self}]: I have receive a NUMBER $number")
      case SpecialMessage(content) =>
        println(s"[${self.path}]: I have receive a special message: $content")
      case SendMessageToYourself(content) => self ! content
      case SayHiTo(ref)                   => ref ! "Hi!"
      case WirelessPhone(content, ref)    => ref forward (content + "s")
    }
  }

  val actorSystem = ActorSystem("actorCapabilities")

  val simpleActor = actorSystem.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "hello, actor"
  simpleActor ! 42

  case class SpecialMessage(content: String)
  simpleActor ! SpecialMessage("some special message")

  case class SendMessageToYourself(content: String)

  simpleActor ! SendMessageToYourself("I am an actor and I am proud of it")

  val alice = actorSystem.actorOf(Props[SimpleActor], "alice")
  val bob = actorSystem.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef)

  alice ! SayHiTo(bob)

  alice ! "Hi!"

  case class WirelessPhone(content: String, ref: ActorRef)

  alice ! WirelessPhone("Hi!", bob)

  /** Exercise 1:
    *
    * a Counter actor:
    *   - increment
    *   - decrement
    *   - Print
    */
  object Counter {
    case class Increment(value: Int)
    case class Decrement(value: Int)
    case object Print
  }
  class Counter extends Actor {
    import Counter._

    var count = 0

    def receive: Receive = {
      case Increment(value) => count += value
      case Decrement(value) => count -= value
      case Print            => println(s"[counter]: my current count is $count")
    }
  }

  val counter = actorSystem.actorOf(Props[Counter], "counter")

  import Counter._
  counter ! Increment(1)
  counter ! Increment(2)
  counter ! Increment(10)
  counter ! Increment(20)
  counter ! Increment(2)

  counter ! Decrement(30)
  counter ! Print

  /** Exercise 2: a Banck account as an actor: Receives:
    *   - Deposit an amount
    *   - Withdraw an amount
    *   - Statement
    *
    * Replies with:
    *   - Respond with Success/Failure
    *
    * interact with some other actor
    */

  object Bank {
    case class Deposit(value: Int)
    case class Withdraw(value: Int)
    case object Statement

    case class Success(message: String)
    case class Failure(reason: String)
  }

  class Bank extends Actor {
    import Bank._

    var funds = 0

    def receive: Receive = {
      case Deposit(value) if (value < 0) =>
        sender() ! Failure("invalid deposit amount")

      case Deposit(value) =>
        funds += value
        sender() ! Success(s"Successfully deposited $value")
      case Withdraw(value) if (value < 0) =>
        sender() ! Failure("invalid withdraw amount")

      case Withdraw(value) if (value > funds) =>
        sender() ! Failure("insuficient funds")

      case Withdraw(value) =>
        funds -= value
        sender() ! Success(s"successfully withdrew $value")
      case Statement =>
        sender() ! s"Your balance is $funds"

    }
  }

  object Person {
    case class LiveTheLive(account: ActorRef)
  }

  class Person extends Actor {
    import Person._
    import Bank._

    def receive: Receive = {
      case LiveTheLive(account) =>
        account ! Deposit(10000)
        account ! Withdraw(90000)
        account ! Withdraw(500)
        account ! Statement
      case message => println(message.toString)
    }
  }

  val account = actorSystem.actorOf(Props[Bank], "bankAccount")
  val person = actorSystem.actorOf(Props[Person], "billionaire")

  import Person._

  person ! LiveTheLive(account)

}
