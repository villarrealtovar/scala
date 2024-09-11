package lesson03Infra

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorLogging
import com.typesafe.config.Config
import org.apache.pekko.dispatch.UnboundedPriorityMailbox
import org.apache.pekko.dispatch.PriorityGenerator
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.PoisonPill
import org.apache.pekko.dispatch.ControlMessage
import com.typesafe.config.ConfigFactory

object L04Mailboxes extends App {

  val system = ActorSystem(
    "MailboxesDemo",
    ConfigFactory.load().getConfig("mailboxesDemo")
  )

  class SimpleActor extends Actor with ActorLogging {
    def receive: Receive = { case message =>
      log.info(message.toString)
    }
  }

  /** Interesting case 1 - custom priority Mailbox P0 -> most important P1 P2 P3
    */

  class SupportTicketPriorityMailbox(
      settings: ActorSystem.Settings,
      config: Config
  ) extends UnboundedPriorityMailbox(PriorityGenerator {
        case message: String if message.startsWith("[P0]") => 0
        case message: String if message.startsWith("[P1]") => 1
        case message: String if message.startsWith("[P2]") => 2
        case message: String if message.startsWith("[P3]") => 3
        case _                                             => 4
      })

  val supportTicketLogger = system.actorOf(
    Props[SimpleActor].withDispatcher("support-ticket-dispatcher")
  )

  // supportTicketLogger ! "[P3] this thing would be nice to have"
  // supportTicketLogger ! "[P0] this needs to be solved NOW!"
  // supportTicketLogger ! "[P1] do this when you have time"
  //
  /** Interesting case 2 - control-aware mailbox we'll use
    * UnboundedControlAwareMailbox
    */

  case object ManagementTicket extends ControlMessage

  // method 1 -
  val controlAwareActor =
    system.actorOf(Props[SimpleActor].withMailbox("control-mailbox"))

  // controlAwareActor ! "[P3] this thing would be nice to have"
  // controlAwareActor ! "[P0] this needs to be solved NOW!"
  // controlAwareActor ! ManagementTicket

  // method 2 - using deployment config

  val alternativeControlAwareActor =
    system.actorOf(Props[SimpleActor], "alternativeControlAwareActor")

  alternativeControlAwareActor ! "[P3] this thing would be nice to have"
  alternativeControlAwareActor ! "[P0] this needs to be solved NOW!"
  alternativeControlAwareActor ! ManagementTicket

}
