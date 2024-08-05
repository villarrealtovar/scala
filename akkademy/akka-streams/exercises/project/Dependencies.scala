import com.lightbend.cinnamon.sbt._
import sbt._

object Version {
  val akkaVer         = "2.6.11"
  val scalaVer        = "2.13.14"
  val scalaTestVer    = "3.2.2"
}

object Dependencies {
  val dependencies = Seq(
    "com.typesafe.akka"       %% "akka-actor"                 % Version.akkaVer withSources(),
    "com.typesafe.akka"       %% "akka-testkit"               % Version.akkaVer withSources(),
    "com.typesafe.akka"       %% "akka-stream"                % Version.akkaVer withSources(),
    "com.typesafe.akka"       %% "akka-stream-testkit"        % Version.akkaVer withSources(),
    Cinnamon.library.cinnamonAkkaStream,
    Cinnamon.library.cinnamonPrometheus,
    Cinnamon.library.cinnamonPrometheusHttpServer,
    "org.scalatest"           %% "scalatest"                  % Version.scalaTestVer % "test"
  )
}
