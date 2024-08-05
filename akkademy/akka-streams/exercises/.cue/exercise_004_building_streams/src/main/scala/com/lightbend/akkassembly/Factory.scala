package com.lightbend.akkassembly

import akka.stream.scaladsl.Sink
import akka.actor.ActorSystem

import scala.concurrent.Future

class Factory(bodyShop: BodyShop,
              paintShop: PaintShop,
              engineShop: EngineShop,
              wheelShop: WheelShop,
              qualityAssurance: QualityAssurance)
             (implicit system: ActorSystem) {
  def orderCars(quantity: Int): Future[Seq[Car]] = {
    bodyShop.cars
      .via(paintShop.paint)
      .via(engineShop.installEngine)
      .via(wheelShop.installWheels)
      .via(qualityAssurance.inspect)
      .take(quantity)
      .runWith(Sink.seq)
  }
}
