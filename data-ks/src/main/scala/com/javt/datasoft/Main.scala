package com.javt.datasoft

import cats.effect.IOApp
import cats.Monad
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router


object Main extends IOApp.Simple {

    def healthEndpoint[F[_] : Monad]: HttpRoutes[F] = {
        val dsl = Http4sDsl[F]
        import dsl.* 
        HttpRoutes.of[F] {
            case GET -> Root / "health" => Ok("All going great!") 
        }
    }

    def allRoutes[F[_] : Monad]: HttpRoutes[F] = healthEndpoint[F]

    def routerWithPathPrefixes = Router(
        "/private" -> healthEndpoint[IO]
    ).orNotFound

    override def run: IO[Unit] = EmberServerBuilder
    .default[IO]
    .withHttpApp(routerWithPathPrefixes)
    .build
    .use(_ => IO.println("Server Ready") *> IO.never)

}