package com.javt.datasoft.http.routes

import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.effect.* 
import cats.implicits.*
import scala.collection.mutable
import com.javt.datasoft.domain.job.Job
import java.util.UUID
import com.javt.datasoft.http.respones.FailureResponse
import com.javt.datasoft.domain.job.JobInfo

class JobRoutes[F[_]: Concurrent] private extends Http4sDsl[F] {

    private val database = mutable.Map[UUID, Job]()

    // POST jobs/offset=x&limit=y { filters } // TODO
    private val allJobRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
      case POST -> Root => Ok(database.values)
    }

    // GET job/uuid
    private val findJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root / UUIDVar(id) => 
        database.get(id) match
          case None => NotFound(FailureResponse(s"Job $id not found.")) 
          case Some(job) => Ok(job)
        
    }

    // POST /jobs/create { jobInfo }
    private def createJob(jobInfo: JobInfo): F[Job] = 
      Job(
        id = UUID.randomUUID(),
        date = System.currentTimeMillis(),
        ownerEmail = "TODO@rockthejvm.com",
        jobInfo = jobInfo,
        active = true 
      ).pure[F]

    private val createJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "create" => 
        for {
          jobInfo <-  req.as[JobInfo]
          job <- createJob(jobInfo)
          resp <- Created(job.id)
        } yield resp
    } 
  
    // PUT /jobs/uuid { jobInfo }
    private val updateJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ PUT -> Root / UUIDVar(id) =>
        database.get(id) match
          case None => NotFound(FailureResponse(s"Cannot update job $id: not found"))
          case Some(job) => for {
            jobInfo <- req.as[JobInfo]
            _ <- database.put(id, job.copy(jobInfo = jobInfo)).pure[F]
            resp <- Ok()
          } yield resp
        
    } 

    // DELETE /jobs/uuid
    private val deleteJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ DELETE -> Root / UUIDVar(id) => 
        database.get(id) match
          case None => NotFound(FailureResponse(s"Cannot delete job $id: not found"))
          case Some(job) => for {
            _ <- database.remove(id).pure[F]
            resp <- Ok()
          } yield resp
        
    } 

    val routes = Router(
     "jobs" -> (allJobRoutes <+> findJobRoute <+> createJobRoute <+> updateJobRoute <+> deleteJobRoute)
    )
}

object JobRoutes {
    def apply[F[_]: Concurrent] = new JobRoutes[F]
}