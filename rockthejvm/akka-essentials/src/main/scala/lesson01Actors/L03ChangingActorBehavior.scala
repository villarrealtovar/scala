package lesson01Actors

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Props
import lesson01Actors.L03ChangingActorBehavior.Mom.MomStart

object L03ChangingActorBehavior extends App {

  object FussyKid {
    case object KidAccept
    case object KidReject

    val HAPPY = "happy"
    val SAD = "sad"
  }

  class FussyKid extends Actor {
    import FussyKid._
    import Mom._

    var state = HAPPY

    def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) if state == HAPPY =>
        sender() ! KidAccept
      case Ask(_) if state == SAD =>
        sender() ! KidReject
    }
  }

  object Mom {
    case class MomStart(kidRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String)

    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  class Mom extends Actor {
    import Mom._
    import FussyKid._

    def receive: Receive = {
      case MomStart(kidRef) =>
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(CHOCOLATE)
        kidRef ! Food(CHOCOLATE)
        kidRef ! Ask("do you want to play?")
      case KidAccept => println("Yay, my kid is happy")
      case KidReject => println("My kid is sad, but as he's healthy!")
    }
  }

  val system = ActorSystem("changingActorBehaviorDemo")

  val fussyKid = system.actorOf(Props[FussyKid])
  val mom = system.actorOf(Props[Mom])

  mom ! MomStart(fussyKid)

  class StatelessFussyKid extends Actor {
    import FussyKid._
    import Mom._

    def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) =>
      case Ask(_)          => sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) => context.unbecome()
      case Ask(_)          => sender() ! KidReject
    }
  }

  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid])

  mom ! MomStart(statelessFussyKid)

  /** Exercise 1.
    *
    * Recreate Counter Actor with context become and NO MUTABLE State
    */
  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }
  class Counter extends Actor {
    import Counter._

    /* def receive: Receive = {
      case Increment(value) => count += value
      case Decrement(value) => count -= value
      case Print            => println(s"[counter]: my current count is $count")
    } */

    def receive: Receive = countReceive(0)

    def countReceive(currentCount: Int): Receive = {
      case Increment =>
        println(s"[countReceive($currentCount)]: incrementing")
        context.become(countReceive(currentCount + 1))
      case Decrement =>
        println(s"[countReceive($currentCount)]: decrementing")
        context.become(countReceive(currentCount - 1))
      case Print =>
        println(
          s"[countReceive($currentCount)]: my current count is $currentCount"
        )
    }

  }

  val counter = system.actorOf(Props[Counter], "counter")

  import Counter._

  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print

  /** Exercise 2.
    *
    * A simplified voting system
    */

  object Citizen {
    case class Vote(candidate: String)
    case object VoteStatusRequest
    case class VoteStatusReply(candidate: Option[String])
  }

  class Citizen extends Actor {
    import Citizen._

    var candidate: Option[String] = None

    override def receive: Receive = {
      case Vote(c)           => candidate = Some(c)
      case VoteStatusRequest => sender() ! VoteStatusReply(candidate)
    }
  }

  object VoteAggregator {
    case class AggregateVotes(citizens: Set[ActorRef])
  }

  class VoteAggregator extends Actor {
    import VoteAggregator._
    import Citizen._

    var stillWaiting: Set[ActorRef] = Set.empty
    var currentStats: Map[String, Int] = Map.empty

    override def receive: Receive = {
      case AggregateVotes(citizens) =>
        stillWaiting = citizens
        citizens.foreach(citizenRef => citizenRef ! VoteStatusRequest)
      case VoteStatusReply(None) => sender() ! VoteStatusRequest
      case VoteStatusReply(Some(candidate)) =>
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate, 0)
        currentStats =
          currentStats + (candidate -> (currentVotesOfCandidate + 1))
        if (newStillWaiting.isEmpty)
          println(s"[VoteAggregator]: poll stats: $currentStats")
        else stillWaiting = newStillWaiting
    }
  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  import Citizen._

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = system.actorOf(Props[VoteAggregator])

  import VoteAggregator._

  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))

  /** Print the status of the votes
    *
    * Martin -> 1 Jonas -> 1 Roland -> 2
    */

}
