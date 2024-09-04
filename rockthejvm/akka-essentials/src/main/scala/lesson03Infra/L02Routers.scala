package lesson03Infra

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorLogging
import org.apache.pekko.actor.Props
import org.apache.pekko.routing.Router
import org.apache.pekko.routing.RoundRobinRoutingLogic
import org.apache.pekko.routing.ActorRefRoutee
import org.apache.pekko.actor.Terminated
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.routing.RoundRobinPool
import org.apache.pekko.routing.FromConfig
import com.typesafe.config.ConfigFactory
import org.apache.pekko.routing.RoundRobinGroup
import org.apache.pekko.routing.Broadcast

object L02Routers extends App {

  /**   1. Manual router
    */
  class Master extends Actor {
    private val slaves = for (i <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave], s"slave-$i")
      context.watch(slave)
      ActorRefRoutee(slave)
    }
    private val router = Router(RoundRobinRoutingLogic(), slaves)

    def receive: Receive = {
      case Terminated(ref) =>
        router.removeRoutee(ref)
        val newSlave = context.actorOf(Props[Slave])
        context.watch(newSlave)
        router.addRoutee(newSlave)
      case message =>
        router.route(message, sender())
    }
  }

  class Slave extends Actor with ActorLogging {
    def receive: Receive = { case message =>
      log.info(s"[${self.path}] - ${message.toString}")
    }
  }

  val system = ActorSystem("RoutersDemo")
  val master = system.actorOf(Props[Master])

  // for (i <- 1 to 10) {
  //   master ! s"[$i] Hello from the world"
  // }

  /** 2. A router actor with its own children a.k.a. Pool Router
    */
  // 2.1 Programmatically (in code)
  val poolMaster =
    system.actorOf(RoundRobinPool(5).props(Props[Slave]), "simplePoolMaster")
  // for (i <- 1 to 10) {
  //   poolMaster ! s"[$i] - Hello from the world"
  // }

  // 2.2 from Configuration
  val system2 =
    ActorSystem("RoutersDemos2", ConfigFactory.load().getConfig("routersDemo"))
  val poolMaster2 =
    system2.actorOf(FromConfig.props(Props[Slave]), "poolMaster2")

  // for (i <- 1 to 10) {
  //   poolMaster2 ! s"[$i] - Hello from the world"
  // }

  /** 3. router with actors created elsewhere Grooup Router
    */
  // ... in another part of my application
  val slaveList = (1 to 5).map(i => system2.actorOf(Props[Slave], s"slave-$i"))

  val slavePaths = slaveList.map(slaveRef => slaveRef.path.toString)

  // 3.1 in the code
  val groupMaster = system2.actorOf(RoundRobinGroup(slavePaths).props())
  // for (i <- 1 to 10) {
  //   groupMaster ! s"[$i] - Hello from the world"
  // }

  // 3.2 from Configuration

  val groupMaster2 = system2.actorOf(FromConfig.props(), "groupMaster2")

  for (i <- 1 to 10) {
    groupMaster2 ! s"[$i] - Hello from the world"
  }

  /** Special messages
    */
  groupMaster2 ! Broadcast("hello, everyone!")

  // PoisonPill and Kill are NOT routed
  // AddRoutee, Remove, Get handled only by the routing actor

}
