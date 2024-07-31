package com.javt.datasoft.http.routes

import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.* 
import cats.implicits.*

class JobRoutes[F[_]: Monad] private extends Http4sDsl[F] {
  private val healthRoute: HttpRoutes[F] = HttpRoutes.of[F] { 
    case GET -> Root =>
        Ok("All going great")
    }


    // POST jobs/offset=x&limit=y { filters } // TODO

    private val allJobRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
      case POST -> Root => Ok("TODO list jobs")
    }

    // GET job/uuid
    private val findJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root / UUIDVar(id) => Ok("TODO find job id")
    }

    // POST /jobs/create { jobInfo }
    private val createJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case POST -> Root => Ok("TODO create job")
    } 
  
    // PUT /jobs/uuid { jobInfo }
    private val updateJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case PUT -> Root / UUIDVar(id) => Ok("TODO put job")
    } 

    // DELETE /jobs/uuid
    private val deleteJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case DELETE -> Root / UUIDVar(id) => Ok("TODO delete job")
    } 

  val routes = Router(
    "jobs" -> (allJobRoutes <+> findJobRoute <+> createJobRoute <+> updateJobRoute <+> deleteJobRoute)
  )
}

object JobRoutes {
    def apply[F[_]: Monad] = new JobRoutes[F]
}