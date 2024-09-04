package lesson03Infra

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorLogging
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Props
import scala.concurrent.duration._
import org.apache.pekko.actor.Cancellable
import org.apache.pekko.actor.Timers

object L01TimersSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging {

    def receive: Receive = { case message =>
      log.info(message.toString)
    }

  }

  val system = ActorSystem("SchedulersTimersDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  system.log.info("Scheduling reminder for simpleActor")

  import system.dispatcher

  system.scheduler.scheduleOnce(1.second) {
    simpleActor ! "reminder"
  } // I could write (system.dispatcher) explicitly, create an implicit value o import the system.dispatcher

  val routine: Cancellable = system.scheduler.schedule(1.second, 2.seconds) {
    simpleActor ! "heartbeat"
  }

  system.scheduler.scheduleOnce(5.seconds) {
    routine.cancel()
  }

}

object L01TimersSchedulersExercise1 extends App {

  val system = ActorSystem("SchedulersTimersExercise")
  import system.dispatcher

  /** Exercise 1. Implement a self-closing actor
    *
    *   - if the actor receives a message (anything), you have 1 second to send
    *     it another message
    *   - if the time window expires, the actor will stop itself
    *   - if you send another message, the time window is reset
    */
  class SelfClosingActor extends Actor with ActorLogging {
    var schedule = createTimeoutWindow()

    def createTimeoutWindow(): Cancellable = {
      context.system.scheduler.scheduleOnce(1.second) {
        self ! "timeout"
      }
    }
    def receive: Receive = {
      case "timeout" =>
        log.info("Stopping myself")
        context.stop(self)
      case message =>
        log.info(s"Received $message, staying alive")
        schedule.cancel()
        schedule = createTimeoutWindow()
    }
  }

  // val selfClosingActor =
  //   system.actorOf(Props[SelfClosingActor], "selfClosingActor")
  // system.scheduler.scheduleOnce(250.millis) {
  //   selfClosingActor ! "ping"
  // }
  //
  // system.scheduler.scheduleOnce(2.seconds) {
  //   system.log.info("sending pong to the selfClosingActor")
  //   selfClosingActor ! "pong"
  // }

  // Timer
  object TimerBasedHeartbeatActor {
    case object TimerKey
    case object Start
    case object Reminder
    case object Stop
  }

  class TimerBasedHeartbeatActor extends Actor with ActorLogging with Timers {
    import TimerBasedHeartbeatActor._
    timers.startSingleTimer(
      TimerKey,
      Start,
      500.millis
    )

    def receive: Receive = {
      case Start =>
        log.info("Boostraping")
        timers.startPeriodicTimer(TimerKey, Reminder, 1.second)
      case Reminder =>
        log.info("I am alive")
      case Stop =>
        log.warning("Stopping!")
        timers.cancel(TimerKey)
        context.stop(self)
    }
  }

  import TimerBasedHeartbeatActor._
  val timerHeartbeatActor =
    system.actorOf(Props[TimerBasedHeartbeatActor], "timerActor")
  system.scheduler.scheduleOnce(5.seconds) {
    timerHeartbeatActor ! Stop
  }
}
