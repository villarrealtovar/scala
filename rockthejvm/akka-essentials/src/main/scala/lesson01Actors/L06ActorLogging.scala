package lesson01Actors

import org.apache.pekko.actor.Actor
import org.apache.pekko.event.Logging
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.ActorLogging

object L06ActorLogging extends App {
  // #1 Explicit logging
  class SimpleActorWithExplicitLogger extends Actor {

    val logger = Logging(context.system, this)

    def receive: Receive = { case message =>
      logger.info(message.toString)
    }
  }

  val system = ActorSystem("LoggingDemo")
  val actor = system.actorOf(Props[SimpleActorWithExplicitLogger])

  actor ! "loggin simple message"

  // #2. ActorLogging
  class ActorWithLogging extends Actor with ActorLogging {
    def receive: Receive = {
      case (a, b) => log.info("Two things: {} and {}", a, b) // interpolate
      case message =>
        log.info(message.toString)
    }
  }

  val simpleActor = system.actorOf(Props[ActorWithLogging])
  simpleActor ! "Logging a simple message by extending ActorLogging"
}
