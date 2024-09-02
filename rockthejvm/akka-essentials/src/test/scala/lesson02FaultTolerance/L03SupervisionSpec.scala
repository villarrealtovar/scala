package lesson02FaultTolerance

import org.apache.pekko.testkit.TestKit
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.ImplicitSender
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.{
  OneForOneStrategy,
  AllForOneStrategy,
  SupervisorStrategy
}
import org.apache.pekko.actor.SupervisorStrategy.Restart
import org.apache.pekko.actor.SupervisorStrategy.Resume
import org.apache.pekko.actor.SupervisorStrategy.Escalate
import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.SupervisorStrategy.Stop
import org.apache.pekko.actor.Terminated
import org.apache.pekko.actor
import org.apache.pekko.testkit.EventFilter

class L03SupervisionSpec
    extends TestKit(ActorSystem("SupervisionSpec"))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll {

  import L03SupervisionSpec.Supervisor
  import L03SupervisionSpec.FuzzyWordCounter
  import L03SupervisionSpec.FuzzyWordCounter._
  import L03SupervisionSpec.NoDeathOnRestartSupervisor
  import L03SupervisionSpec.AllForOneStrategySupervisor

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A supervisor" should {
    "resume its child in case of a minor fault" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FuzzyWordCounter]

      val child = expectMsgType[ActorRef]

      child ! "I love Pekko"
      child ! Report
      expectMsg(3)

      child ! "Pekko is awesome because I am learning to think in a new whole new way" // Throws RuntimeException
      child ! Report
      expectMsg(3)
    }

    "restart its child in case of an empty sentence" in {

      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FuzzyWordCounter]

      val child = expectMsgType[ActorRef]

      child ! "I love Pekko"
      child ! Report
      expectMsg(3)

      child ! "" // Throws NullPointerException
      child ! Report
      expectMsg(0)
    }

    "terminate its child in case of a major error" in {

      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FuzzyWordCounter]

      val child = expectMsgType[ActorRef]

      watch(child)
      child ! "pekko is nice"
      val terminatedMesage = expectMsgType[Terminated]
      assert(terminatedMesage.actor == child)
    }

    "escalate an error when it doesn't know what to do" in {

      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FuzzyWordCounter]

      val child = expectMsgType[ActorRef]
      watch(child)
      child ! 42
      val terminatedMesage = expectMsgType[Terminated]
      assert(terminatedMesage.actor == child)
    }

  }

  "A kinder supervisor " should {
    "not kill children in case it's restarted or escalate failed" in {

      val supervisor =
        system.actorOf(Props[NoDeathOnRestartSupervisor], "supervisor")
      supervisor ! Props[FuzzyWordCounter]

      val child = expectMsgType[ActorRef]
      child ! "Pekko is cool"
      child ! Report
      expectMsg(3)

      child ! 42
      child ! Report

    }
  }

  "An all-for-one supervisor" should {
    "apply the all-for-one strategy" in {
      val supervisor = system.actorOf(
        Props[AllForOneStrategySupervisor],
        "AllForOneSupervisor"
      )
      supervisor ! Props[FuzzyWordCounter]
      val child = expectMsgType[ActorRef]

      supervisor ! Props[FuzzyWordCounter]
      val secondChild = expectMsgType[ActorRef]

      secondChild ! "Testing Supervision"
      secondChild ! Report
      expectMsg(2)

      EventFilter[NullPointerException]() intercept {
        child ! ""
      }
      Thread.sleep(500) // not best practice
      secondChild ! Report
      expectMsg(0)

    }
  }
}

object L03SupervisionSpec {

  class Supervisor extends Actor {

    override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException         => Resume
      case _: Exception                => Escalate
    }

    def receive: Receive = { case props: Props =>
      val childRef = context.actorOf(props)
      sender() ! childRef
    }
  }

  class NoDeathOnRestartSupervisor extends Supervisor {
    override def preStart(): Unit = {
      // empty
    }
  }

  class AllForOneStrategySupervisor extends Supervisor {

    override val supervisorStrategy = AllForOneStrategy() {
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException         => Resume
      case _: Exception                => Escalate
    }
  }

  object FuzzyWordCounter {
    case object Report
  }

  class FuzzyWordCounter extends Actor {
    import FuzzyWordCounter._

    var words = 0

    def receive: Receive = {
      case Report => sender() ! words
      case ""     => throw new NullPointerException("sentence is empty")
      case sentence: String if (sentence.length > 20) =>
        throw new RuntimeException("sentence is too big")
      case sentence: String if (sentence.head.isLower) =>
        throw new IllegalArgumentException("sentence must start with uppercase")
      case sentence: String => words += sentence.split(" ").length
      case _                => throw new Exception("can only receive strings")
    }
  }
}
