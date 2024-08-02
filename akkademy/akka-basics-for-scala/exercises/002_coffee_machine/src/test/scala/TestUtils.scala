import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import org.slf4j.event.Level

object TestUtils {

  def expectedInfoLog(message: String): CapturedLogEvent  = {
    CapturedLogEvent(
      level = Level.INFO,
      message,
      cause = Option.empty,
      marker = Option.empty
    )
  }

  def offsetCapturedLogEvent[T](testKit: BehaviorTestKit[T], offsetFromEnd: Int): CapturedLogEvent = {
    testKit.logEntries().reverse.applyOrElse[Int, CapturedLogEvent](
      offsetFromEnd,
      _ => {
        throw new RuntimeException(s"Invalid offsetFromEnd parameter, $offsetFromEnd is not within [0, logsSize]")
      }
    )
  }
}
