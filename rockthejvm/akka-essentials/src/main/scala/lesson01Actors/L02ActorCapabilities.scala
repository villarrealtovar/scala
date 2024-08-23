package lesson01Actors

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.ActorRef

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
}
