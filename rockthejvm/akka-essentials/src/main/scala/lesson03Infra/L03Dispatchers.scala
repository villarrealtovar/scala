package lesson03Infra

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorLogging
import org.apache.pekko.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.Props
import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.ExecutionContextExecutor

object L03Dispatchers extends App {

  class Counter extends Actor with ActorLogging {
    var counter = 0

    def receive: Receive = { case message =>
      counter += 1
      log.info(s"${self.path} - [$counter] $message")
    }
  }

  val system = ActorSystem(
    "DispatchersDemo"
    // ConfigFactory.load().getConfig("dispatchersDemo")
  )

  // method 1 - programmatic/in code
  val actors =
    for (i <- 1 to 10)
      yield system.actorOf(
        Props[Counter].withDispatcher("my-dispatcher"),
        s"Counter-$i"
      )

  // val r = new Random()
  // for (i <- 1 to 1000) {
  //   actors(r.nextInt(10)) ! i
  // }

  // method 2 -  from Config

  val liftCodeActor = system.actorOf(
    Props[Counter],
    "lifeCode"
  ) // <- lifeCode is inside of application.conf flie

  /** Dispatchers implement ExecutionContext trait
    */

  class DatabaseActor extends Actor with ActorLogging {

    // implicit val executionContext: ExecutionContextExecutor = context.dispatcher
    // Solution #1
    implicit val executionContext: ExecutionContextExecutor =
      context.system.dispatchers.lookup("my-dispatcher")

    // Solution #2
    // use a Router
    def receive: Receive = { case message =>
      Future {
        // wait
        Thread.sleep(5000)
        log.info(s"Success: $message")
      }
    }
  }

  val dbActor = system.actorOf(Props[DatabaseActor])
  // dbActor ! "the meaning of life is 42"

  val nonBlockingActor = system.actorOf(Props[Counter])
  for (i <- 1 to 1000) {
    val message = s"important message-$i"
    dbActor ! message
    nonBlockingActor ! message
  }
}
