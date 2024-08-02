import scala.collection.mutable

import akka.actor.testkit.typed._
import akka.actor.testkit.typed.scaladsl._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import com.lightbend.training.Barista.OrderCoffee
import com.lightbend.training.CoffeeMachine.CoffeeMachineCommand
import com.lightbend.training._

class BaristaTests extends AnyWordSpecLike with Matchers with LogCapturing {

  "Barista" should {
    "log receiving order" in {
      val whom1 = "Bart"
      val coffee1 = Akkaccino
      val whom2 = "Lisa"
      val coffee2 = MochaPlay

      val testKit: BehaviorTestKit[OrderCoffee] = BehaviorTestKit(Barista())

      testKit.clearLog()
      testKit.run(OrderCoffee(whom1, coffee1))
      testKit.run(OrderCoffee(whom2, coffee2))

      val allLogEntries: Seq[CapturedLogEvent] = testKit.logEntries()

      val expectedOrders: mutable.Map[String, Coffee] = mutable.Map()
      expectedOrders.put(whom1, coffee1)
      expectedOrders.put(whom2, coffee2)

      val expectedLogEvent: CapturedLogEvent = TestUtils.expectedInfoLog(s"Orders:${Barista.printOrders(expectedOrders.toSet)}")

      allLogEntries(1) shouldBe expectedLogEvent
    }

    "spawn a child actor CoffeeMachine with as actor name 'coffee-machine'" in {
      val testKit: BehaviorTestKit[OrderCoffee] = BehaviorTestKit(Barista())

      testKit.expectEffectType[Effect.Spawned[CoffeeMachineCommand]].childName shouldBe "coffee-machine"
    }
  }
}
