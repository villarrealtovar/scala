package playground

import org.apache.pekko.actor.ActorSystem

object Playground extends App {

  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
}
