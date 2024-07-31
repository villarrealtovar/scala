package com.javt.datasoft.http

import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.effect.* 
import cats.implicits.*
import com.javt.datasoft.http.routes.*

class HttpApi[F[_]: Concurrent] private {
    private val healthRoutes = HealthRoutes[F].routes
    private val jobRoutes = JobRoutes[F].routes
    private val pipelineRoutes = PipelineRoutes[F].routes

    val endpoints = Router(
        "/api" -> (healthRoutes <+> jobRoutes <+> pipelineRoutes)
    )
  
}

object HttpApi {
    def apply[F[_]: Concurrent] = new HttpApi[F]
}
