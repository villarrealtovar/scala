scalaVersion := "2.13.14"
ThisBuild / organization := "com.example"

lazy val hello = (project in file("."))
  .settings(
    name := "FP Course"
  )

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.3" cross CrossVersion.full
)

libraryDependencies += "org.typelevel" %% "cats-core" % "2.7.0"
libraryDependencies += "org.typelevel" %% "cats-laws" % "2.1.1"
libraryDependencies += "org.typelevel" %% "discipline-core" % "1.0.0"
libraryDependencies += "org.typelevel" %% "discipline-scalatest" % "2.1.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.0"
