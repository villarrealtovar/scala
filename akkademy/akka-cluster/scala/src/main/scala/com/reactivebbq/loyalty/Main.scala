package com.reactivebbq.loyalty

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import org.slf4j.LoggerFactory
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.management.scaladsl.AkkaManagement

object Main extends App {
  val log = LoggerFactory.getLogger(this.getClass)

  val Opt = """-D(\S+)=(\S+)""".r
  args.toList.foreach {
    case Opt(key, value) =>
      log.info(s"Config Override: $key = $value")
      System.setProperty(key, value)
  }

  implicit val system: ActorSystem = ActorSystem("Loyalty")

  AkkaManagement(system).start()

  val rootPath = Paths.get("tmp")
  val loyaltyRepository: LoyaltyRepository = new FileBasedLoyaltyRepository(rootPath)(system.dispatcher)

   
  val loyaltyActorSupervisor = ClusterSharding(system).start(
     "loyalty",
     LoyaltyActor.props(loyaltyRepository),
     ClusterShardingSettings(system),
     LoyaltyActorSupervisor.idExtractor,
     LoyaltyActorSupervisor.shardIdExtractor
   )

  val loyaltyRoutes = new LoyaltyRoutes(loyaltyActorSupervisor)(system.dispatcher)

  Http().newServerAt(
    "localhost",
    system.settings.config.getInt("akka.http.server.default-http-port")
  ).bind(loyaltyRoutes.routes)
}
