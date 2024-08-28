package lesson02FaultTolerance

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorLogging
import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.PoisonPill
import org.apache.pekko.actor.Kill
import org.apache.pekko.actor.Terminated

object L01StartingStoppingActors extends App {

  object Parent {
    case class StartChild(name: String)
    case class StopChild(name: String)
    case object Stop
  }

  class Parent extends Actor with ActorLogging {
    import Parent._

    def receive: Receive = withChildren(Map.empty)

    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")
        context.become(
          withChildren(children + (name -> context.actorOf(Props[Child], name)))
        )
      case StopChild(name) =>
        log.info(s"Stopping child $name")
        val childOption = children.get(name)
        childOption.foreach(childRef => context.stop(childRef))
      case Stop =>
        log.info("Stopping myself")
        context.stop(self)
      case message => log.info(message.toString)
    }
  }

  class Child extends Actor with ActorLogging {
    def receive: Receive = { case message =>
      log.info(message.toString)
    }
  }

  // Method #1 - using context stop
  val system = ActorSystem("StoppingActorsDemo")
  val parent = system.actorOf(Props[Parent], "parent")

  import Parent._
  parent ! StartChild("child1")

  Thread.sleep(100)
  val child = system.actorSelection("/user/parent/child1")

  child ! "hi kid!"

  parent ! StopChild("child1")
  // for (_ <- 1 to 50) child ! "are you still there?"

  parent ! StartChild("child2")

  Thread.sleep(100)
  /* val child2 = system.actorSelection("/user/parent/child2")
  child2 ! "hi second child"
  parent ! Stop
  for (_ <- 1 to 10) parent ! "parent, are you still there?"
  for (i <- 1 to 100) child2 ! s"[$i] second kid, are you still live?" */

  // Method #2 - using messages
  /* val looserActor = system.actorOf(Props[Child])
  looserActor ! "hello looserActor"
  looserActor ! PoisonPill
  looserActor ! "looser actor, are you still there?"

  val abruptlyTerminatedActor = system.actorOf(Props[Child])
  abruptlyTerminatedActor ! "you are about to be terminated"
  abruptlyTerminatedActor ! Kill
  abruptlyTerminatedActor ! "you have been terminated" */

  // Death Watch
  class Watcher extends Actor with ActorLogging {
    import Parent._

    def receive: Receive = {
      case StartChild(name) =>
        val child = context.actorOf(Props[Child], name)
        log.info(s"Started and watching child $name - ${self.path}")
        context.watch(child)
      case Terminated(actorRef) =>
        println("hello Terminated")
        log.info(s"the reference that I'm watching $actorRef has been stopped")
    }
  }

  val watcher = system.actorOf(Props[Watcher], "watcher")
  watcher ! StartChild("watchedChild")
  val watchedChild = system.actorSelection("/user/watcher/watchedChild")
  Thread.sleep(500)

  watchedChild ! PoisonPill

}
