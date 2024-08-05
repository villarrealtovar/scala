package com.lightbend.akkassembly

import akka.{NotUsed, Done}
import akka.event.LoggingAdapter
import akka.stream.scaladsl.{Flow, Sink}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class Auditor {
  val count: Sink[Any, Future[Int]] = Sink.fold[Int, Any](0) {
    case (c, _) => c + 1
  }

  def log(implicit loggingAdapter: LoggingAdapter): Sink[Any, Future[Done]] = Sink.foreach { elem =>
    loggingAdapter.debug(elem.toString)
  }

  def sample(sampleSize: FiniteDuration): Flow[Car, Car, NotUsed] = {
    Flow[Car].takeWithin(sampleSize)
  }
}
