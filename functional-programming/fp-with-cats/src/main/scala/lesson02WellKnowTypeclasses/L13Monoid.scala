package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._

object L13Monoid extends App {

  case class Speed(metersPerSecond: Double) {
    def kilometersPerSec: Double = metersPerSecond / 1000.0

    def milesPerSec: Double = metersPerSecond / 1609.34
  }

  object Speed {
    def addSpeeds(s1: Speed, s2: Speed): Speed =
      Speed(s1.metersPerSecond + s2.metersPerSecond)

    // implicit val monoidSpeed: Monoid[Speed] = new Monoid[Speed] {
    //   override def combine(x: Speed, y: Speed): Speed = addSpeeds(x, y)
    //
    //   override def empty: Speed = Speed(0)
    // }

    // implicit val monoidSpeed: Monoid[Speed] = Monoid.instance(
    //   Speed(0),
    //   (x, y) => addSpeeds(x, y)
    // )

    implicit val monoidSpeed: Monoid[Speed] =
      Monoid.instance(Speed(0), addSpeeds)

    implicit val eqSpeed: Eq[Speed] = Eq.fromUniversalEquals

  }

  println(Monoid[Speed].combine(Speed(1000), Speed(2000)))
  println(Monoid[Speed].empty)

  println(Monoid[Speed].combine(Speed(1000), Monoid[Speed].empty))

  println(Speed(1000) |+| Speed(2000))

  println(Monoid[Speed].combineAll(List(Speed(100), Speed(200), Speed(300))))
  println(List(Speed(100), Speed(200), Speed(300)).combineAll)

  println(Monoid[Speed].isEmpty(Speed(100)))
  println(Monoid[Speed].isEmpty(Speed(0)))

  /** Exercise 1
    *
    * val sumMonoid[Int]: Monoid[Int] = ???
    */

  val sumMonoid: Monoid[Int] = Monoid.instance[Int](
    0,
    _ + _
  )

  /** Exercise 2
    *
    * val minMonoid: Monoid[Int] = ???
    */

  val minMonoid: Monoid[Int] = Monoid.instance(
    Int.MaxValue,
    _ min _
  )

  /** Exercise 3
    *
    * def listMonoid[A]: Monoid[List[A]] = ???
    */
  def listMonoid[A]: Monoid[List[A]] = Monoid.instance(
    List.empty[A],
    _ ++ _
  )

  /** Exercise 4
    *
    * val stringMonoid: Monoid[String] = ???
    */
  val stringMonoid: Monoid[String] = Monoid.instance(
    "",
    _ + _
  )

  println(sumMonoid.combine(3, 4))
  println(minMonoid.combine(6, 2))
  println(minMonoid.combine(6, minMonoid.empty))
  println(minMonoid.combine(minMonoid.empty, 6))
  println(listMonoid[Boolean].combine(List(true, false), List(false, true)))
  println(stringMonoid.combine("hello ", "world!"))

}
