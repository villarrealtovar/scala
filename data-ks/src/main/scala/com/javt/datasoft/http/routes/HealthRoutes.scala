package com.javt.datasoft.http.routes

import cats.Monad
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class HealthRoutes {
    def healthEndpoint[F[_] : Monad]: HttpRoutes[F] = {
        val dsl = Http4sDsl[F]
        import dsl.* 
        HttpRoutes.of[F] {
            case GET -> Root / "health" => Ok("All going great!") 
        }
    }
}