import sbt.*

object Dependencies {

  object Version {
    val catsEffect = "3.3.14"
    val circe = "0.14.1"
    val doobie = "1.0.0-M5"
    val http4s = "0.23.15"
    val kafkaStreams = "3.4.0"
    val postgresSql = "42.3.5"
    val slf4jSimple = "2.0.0"
  }

  object Libraries {
    def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % Version.http4s
    def doobie(artifact: String): ModuleID = "org.tpolecat" %% s"doobie-$artifact" % Version.doobie

    lazy val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect
    lazy val circeGeneric = "io.circe" %% "circe-generic" % Version.circe 
    lazy val doobieCore = doobie("core")
    lazy val doobieHikari = doobie("hikari")
    lazy val doobiePostgres = doobie("postgres")
    lazy val http4sCirce = http4s("circe")
    lazy val http4sServer =  http4s("ember-server")
    lazy val http4sDsl = http4s("dsl")
    lazy val kafkaStreams = "org.apache.kafka" %% "kafka-streams-scala" % Version.kafkaStreams
    lazy val postgresql = "org.postgresql" % "postgresql" % Version.postgresSql
    lazy val slf4j = "org.slf4j" % "slf4j-simple" % Version.slf4jSimple
    
  }

}