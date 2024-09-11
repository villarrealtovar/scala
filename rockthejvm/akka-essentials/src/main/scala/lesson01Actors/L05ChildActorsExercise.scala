package lesson01Actors

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.ActorSystem

object L05ChildActorsExercise extends App {

  // Distributed word counting

  object WordCounterMaster {
    case class Initialize(nChildren: Int)
    case class WordCountTask(id: Int, text: String)
    case class WordCountReply(id: Int, count: Int)
  }

  class WordCounterMaster extends Actor {
    import WordCounterMaster._

    def receive: Receive = { case Initialize(nChildren) =>
      println("[master] initializing...")
      val childrenRefs: Seq[ActorRef] =
        for (i <- (0 until nChildren))
          yield context.actorOf(Props[WordCounterWorker], s"worker-$i")
      context.become(withChildrenRefs(childrenRefs, 0, 0, Map.empty))
    }

    def withChildrenRefs(
        childrenRefs: Seq[ActorRef],
        curIndexChild: Int,
        curTaskId: Int,
        requestMap: Map[Int, ActorRef]
    ): Receive = {
      case text: String =>
        println(
          s"[master] I have received: $text - it will send it to child $curIndexChild"
        )
        val originalSender = sender()
        val childRef = childrenRefs(curIndexChild)
        childRef ! WordCountTask(curTaskId, text)
        context.become(
          withChildrenRefs(
            childrenRefs,
            (curIndexChild + 1) % childrenRefs.length,
            curTaskId + 1,
            requestMap + (curTaskId -> originalSender)
          )
        )
      case WordCountReply(id, count) =>
        println(s"[master] I have received a reply for task $id with $count")
        requestMap(id) ! count
        context.become(
          withChildrenRefs(
            childrenRefs,
            curIndexChild,
            curTaskId,
            requestMap - id
          )
        )
    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._

    def receive: Receive = { case WordCountTask(id, text) =>
      println(s"${self.path} I have received a task $id with `$text`")
      sender() ! WordCountReply(id, text.split(" ").length)
    }
  }

  class TestActor extends Actor {
    import WordCounterMaster._

    def receive: Receive = {
      case "go" =>
        val master = context.actorOf(Props[WordCounterMaster], "master")
        master ! Initialize(3)
        val texts = List("I love akka", "Scala is super dope", "yes", "me too")
        texts.foreach(text => master ! text)

      case count: Int =>
        println(s"[Test Actor] I have received a reply: $count")
    }
  }

  val system = ActorSystem("roundRobinCountExercise")
  val testActor = system.actorOf(Props[TestActor], "testActor")
  testActor ! "go"
}
