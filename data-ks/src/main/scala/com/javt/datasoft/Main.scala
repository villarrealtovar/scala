package com.javt.datasoft

import cats.effect.IOApp
import cats.Monad
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import com.javt.datasoft.http.routes.HealthRoutes


object Main extends IOApp.Simple {

    override def run: IO[Unit] = EmberServerBuilder
    .default[IO]
    .withHttpApp(HealthRoutes[IO].routes.orNotFound)
    .build
    .use(_ => IO.println("Server Ready") *> IO.never)

}