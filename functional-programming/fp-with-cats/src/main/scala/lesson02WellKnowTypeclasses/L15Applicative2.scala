package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._

object L15Applicative2 extends App {
  // Applicatives = Functors + the pure method

  val aList = Applicative[List].pure(2)
  println(aList)

  val aOption = Applicative[Option].pure(3)
  println(aOption)

  val aSweetList = 2.pure[List]
  val aSweetOption = 2.pure[Option]

  import cats.data.Validated

  type ErrorsOr[T] = Validated[List[String], T]

  val aValidValue: ErrorsOr[Int] = Validated.valid(42)

  val aModifiedValidated: ErrorsOr[Int] = aValidValue.map(_ + 1)

  val validatedApplicative = Applicative[ErrorsOr].pure(42)
  println(validatedApplicative)

  /** TODO: Thought experiment
    */
  def productWithApplicatives[W[_], A, B](wa: W[A], wb: W[B])(implicit
      applicative: Applicative[W]
  ): W[(A, B)] = applicative.map2(wa, wb)((a, b) => (a, b))

  def ap[W[_], A, B](wf: W[A => B])(wa: W[A]): W[B] = ???
  def productWithApplicatives2[W[_], A, B](wa: W[A], wb: W[B])(implicit
      applicative: Applicative[W]
  ): W[(A, B)] = {
    val functionWrapper: W[B => (A, B)] =
      applicative.map(wa)(a => (b: B) => (a, b))
    ap(functionWrapper)(wb)
  }

  def productWithApplicatives3[W[_], A, B](wa: W[A], wb: W[B])(implicit
      applicative: Applicative[W]
  ): W[(A, B)] = {
    val functionWrapper: W[B => (A, B)] =
      applicative.map(wa)(a => (b: B) => (a, b))
    applicative.ap(functionWrapper)(wb)

  }
}
