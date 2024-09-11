package lesson02FaultTolerance

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorLogging
import scala.io.Source
import java.io.File
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Props
import org.apache.pekko.pattern.BackoffSupervisor
import org.apache.pekko.pattern.Backoff
import scala.concurrent.duration._
import org.apache.pekko.actor.OneForOneStrategy
import org.apache.pekko.actor.SupervisorStrategy.Stop

object L04BackoffSupervisorPattern extends App {

  object FileBasedPersistentActor {
    case object ReadFile
  }

  class FileBasedPersistentActor extends Actor with ActorLogging {
    import FileBasedPersistentActor._

    var datasource: Source = null

    def receive: Receive = { case ReadFile =>
      if (datasource == null)
        datasource = Source.fromFile(
          new File("src/main/resources/testFiles/important_data.txt")
        )

      log.info("I've read some IMPORTANT data: " + datasource.getLines().toList)
    }

    override def preStart(): Unit = {
      log.info("Persistent actor starting")
    }

    override def postStop(): Unit = {
      log.warning("Persistent actor has stopped")
    }

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      log.warning("Persistent actor restarting")
    }
  }

  import FileBasedPersistentActor._

  val system = ActorSystem("BackoffSupervisorPattern")
  // val simpleActor =
  //   system.actorOf(Props[FileBasedPersistentActor], "simpleActor")

  // simpleActor ! ReadFile

  val simpleSupervisorProps = BackoffSupervisor.props(
    Backoff.onFailure(
      Props[FileBasedPersistentActor],
      "simpleBackoffFactor",
      3.seconds,
      30.seconds,
      0.2
    )
  )

  // val simpleActorSupervised =
  //   system.actorOf(simpleSupervisorProps, "simpleSupervisor")
  // simpleActorSupervised ! ReadFile

  val stopSupervisedProps = BackoffSupervisor.props(
    Backoff
      .onStop(
        Props[FileBasedPersistentActor],
        "stopBackoffActor",
        3.seconds,
        30.seconds,
        0.2
      )
      .withSupervisorStrategy(OneForOneStrategy() { case _ =>
        Stop
      })
  )

  // val simpleStopSupervised =
  //   system.actorOf(stopSupervisedProps, "stopSupervised")
  // simpleStopSupervised ! ReadFile

  class EagerFileBasedPersistentActor extends FileBasedPersistentActor {
    override def preStart(): Unit = {
      log.info("Eager Actor starting")

      datasource = Source.fromFile(
        new File("src/main/resources/testFiles/important_data.txt")
      )
    }
  }

  // val eagerActor = system.actorOf(Props[EagerFileBasedPersistentActor]) // throws an ActorInitilizationException

  val repeatedSupervisorProps = BackoffSupervisor.props(
    Backoff.onStop(
      Props[EagerFileBasedPersistentActor],
      "eagerActor",
      1.second,
      30.seconds,
      0.1
      )
    )

  val repeatedSupervisor = system.actorOf(repeatedSupervisorProps, "eagerSupervisor")

}
