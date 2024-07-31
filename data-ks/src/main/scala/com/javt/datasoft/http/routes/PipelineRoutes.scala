package com.javt.datasoft.http.routes

import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.* 
import cats.implicits.*

class PipelineRoutes[F[_]: Monad] private extends Http4sDsl[F] {
    // POST pipelines/offset=x&limit=y { filters } // TODO
    private val allPipelineRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
      case POST -> Root => Ok("TODO list pipelines")
    }

    // GET pipelines/uuid
    private val findPipelineRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root / UUIDVar(id) => Ok("TODO find pipelines id")
    }

    // POST /pipelines/create { jobInfo }
    private val createPipelineRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case POST -> Root => Ok("TODO create pipelines")
    } 
  
    // PUT /pipelines/uuid { jobInfo }
    private val updatePipelineRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case PUT -> Root / UUIDVar(id) => Ok("TODO put pipelines")
    } 

    // DELETE /pipelines/uuid
    private val deleteJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case DELETE -> Root / UUIDVar(id) => Ok("TODO delete pipelines")
    } 

    val routes = Router(
      "pipelines" -> (allPipelineRoutes <+> findPipelineRoute <+> createPipelineRoute <+> updatePipelineRoute <+> deleteJobRoute)
    )
}

object PipelineRoutes {
    def apply[F[_]: Monad] = new PipelineRoutes[F]
}