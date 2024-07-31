import Dependencies._


ThisBuild / version := "0.1"
ThisBuild / autoScalaLibrary := false

val scala3Version = "3.3.3"


lazy val server = (project in file("."))
.settings(
    scalaVersion := scala3Version,
    name := "Build Kafka Stream Pipelines",
    organization := "com.javt",
    libraryDependencies ++= Seq(
        Libraries.catsEffect,
        Libraries.circeGeneric,
        Libraries.doobieCore,
        Libraries.doobieHikari,
        Libraries.doobiePostgres,
        Libraries.http4sCirce,
        Libraries.http4sDsl,
        Libraries.http4sServer,
        // Libraries.kafkaStreams,
        Libraries.postgresql,
        Libraries.slf4j
    ) 
)
