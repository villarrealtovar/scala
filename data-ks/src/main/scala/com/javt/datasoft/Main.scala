package com.javt.datasoft

import cats.effect.IOApp
import cats.Monad
import cats.implicits.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import com.javt.datasoft.http.routes.HealthRoutes
import com.javt.datasoft.config.EmberConfig
import pureconfig.error.ConfigReaderException
import com.javt.datasoft.config.syntax.*
import pureconfig.ConfigSource


object Main extends IOApp.Simple {

    val configSource = ConfigSource.default.load[EmberConfig]

    override def run: IO[Unit] = 
        ConfigSource.default.loadF[IO, EmberConfig].flatMap{ config =>
                EmberServerBuilder
                .default[IO]
                .withHost(config.host) 
                .withPort(config.port)
                .withHttpApp(HealthRoutes[IO].routes.orNotFound)
                .build
                .use(_ => IO.println("Server Ready") *> IO.never)
        }
}