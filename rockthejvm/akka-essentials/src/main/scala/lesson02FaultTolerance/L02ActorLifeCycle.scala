package lesson02FaultTolerance

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorLogging
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.PoisonPill

object L02ActorLifeCycle extends App {
  object LifeCycleActor {
    case object StartChild
  }

  class LifeCycleActor extends Actor with ActorLogging {
    import LifeCycleActor._

    def receive: Receive = { case message =>
      context.actorOf(Props[LifeCycleActor], "child")
    }

    override def preStart(): Unit = log.info(s"${self.path}: I'm starting")

    override def postStop(): Unit = log.info(s"${self.path}: I have stopped")
  }

  val system = ActorSystem("LifecycleDemo")
  // val parent = system.actorOf(Props[LifeCycleActor], "parent")

  // import LifeCycleActor._
  // parent ! StartChild
  // parent ! PoisonPill

  /** Restart
    */

  object Parent {
    case object FailChild
    case object CheckChild
  }

  class Parent extends Actor {
    import Parent._
    import Child._

    private val child = context.actorOf(Props[Child], "supervisedChild")

    def receive: Receive = {
      case FailChild =>
        child ! Fail
      case CheckChild =>
        child ! Check

    }
  }

  object Child {
    case object Fail
    case object Check
  }
  class Child extends Actor with ActorLogging {
    import Child._

    def receive: Receive = {
      case Fail =>
        log.warning("child will fail now")
        throw new RuntimeException("I failed")
      case Check =>
        log.info("alive and kicking")
    }

    override def preStart(): Unit = log.info("supervised child is starting")
    override def postStop(): Unit = log.info("supervised child stopped")
    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.info(
        s"${self.path} - supervised child actor is restarting because of ${reason.getMessage}"
      )
    override def postRestart(reason: Throwable): Unit =
      log.info(s"${self.path}- supervised child actor restarted")
  }

  import Parent._
  val supervisor = system.actorOf(Props[Parent], "supervisorParent")
  supervisor ! FailChild
  supervisor ! CheckChild
}
