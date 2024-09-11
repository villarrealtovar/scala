package lesson01Actors

import org.apache.pekko.actor.{ActorSystem, Actor}
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.ActorRef

object L01ActorsIntro extends App {
  val actorSystem = ActorSystem("firstActorSystem")

  println(actorSystem.name)

  // words count actor
  class WordCountActor extends Actor {
    var totalWords: Int = 0

    def receive: PartialFunction[Any, Unit] = {
      case message: String =>
        println(s"[word counter]: I've received $message")
        totalWords + message.split(" ").length
      case msg =>
        println(s"[word counter]: I cannot understand ${msg.toString}")
    }
  }

  val wordCounter: ActorRef =
    actorSystem.actorOf(Props[WordCountActor], "wordCounter")

  val anotherWordCounter: ActorRef =
    actorSystem.actorOf(Props[WordCountActor], "anotherWordCounter")

  wordCounter ! "I'm learning Akka/Pekko and it's pretty damn cool"
  anotherWordCounter ! "A different message"

  class Person(name: String) extends Actor {
    def receive: Receive = {
      case "hi" => println(s"Hi, my name is $name ")
      case _    =>
    }
  }

  object Person {
    def props(name: String) = Props(new Person(name))
  }

  // val personActor = actorSystem.actorOf(Props(new Person("Andres")))

  val personActor = actorSystem.actorOf(Person.props("Andres"))

  personActor ! "hi"
}
