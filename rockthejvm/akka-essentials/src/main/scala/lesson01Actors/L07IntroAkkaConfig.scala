package lesson01Actors

import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.ActorLogging
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.Actor

object L07IntroAkkaConfig extends App {

  class SimpleLogginActor extends Actor with ActorLogging {
    def receive: Receive = { case message =>
      log.info(message.toString)
    }
  }

  // 1. Inline configuration

  val configString = """
    pekko {
      loglevel = "DEBUG" 
    }
  """.stripMargin

  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("configurationDemo", ConfigFactory.load(config))
  val actor = system.actorOf(Props[SimpleLogginActor])

  actor ! "A message to remember"

  // 2. File configuration (application.conf)
  val defaultConfigFileSystem = ActorSystem("DefaultConfigFileDemo")
  val defaultConfigActor =
    defaultConfigFileSystem.actorOf(Props[SimpleLogginActor])

  defaultConfigActor ! "Remember me"

  // 3. Separated configurations in the same file (application.conf)
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigSystem = ActorSystem("SpecialConfigDemo", specialConfig)
  val specialConfigActor = specialConfigSystem.actorOf(Props[SimpleLogginActor])

  specialConfigActor ! "Remember me, I am special"

  // 4. separated config in another file
  val separatedConfig =
    ConfigFactory.load("secretFolder/secretConfiguration.conf")
  println(
    s"separated config log level: ${separatedConfig.getString("pekko.loglevel")}"
  )

  // 5. different file formats (JSON, Properties)
  val jsonConfig = ConfigFactory.load("json/jsonConfig.json")
  println(s"json config: ${jsonConfig.getString("aJsonProperty")}")
  println(s"json config: ${jsonConfig.getString("pekko.loglevel")}")

  val propsConfig = ConfigFactory.load("props/propsConfiguration.properties")
  println(s"props config: ${propsConfig.getString("my.simpleProperty")}")
  println(s"props config: ${propsConfig.getString("pekko.loglevel")}")

}
