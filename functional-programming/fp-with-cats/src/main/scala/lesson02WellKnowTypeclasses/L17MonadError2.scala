package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._
import scala.util.Try
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.Future

object L17MonadError2 extends App {

  trait MyApplicativeError[F[_], E] extends Applicative[F] {

    def raiseError[A](e: E): F[A]
    def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]
    def handleError[A](fa: F[A])(f: E => A): F[A] =
      handleErrorWith(fa)(e => pure(f(e)))
  }

  trait MyMonadError[F[_], E] extends MyApplicativeError[F, E] with Monad[F] {
    def ensure[A](fa: F[A])(error: E)(predicate: A => Boolean): F[A]
  }

  type ErrorOr[A] = Either[String, A]

  val eitherME = MonadError[ErrorOr, String]

  val success: ErrorOr[Int] =
    eitherME.pure(42) // Right(42): Either[String, Int]

  val failure: ErrorOr[Int] = eitherME.raiseError[Int](
    "somethingError"
  ) // Left("somethingError"): Either[String, Int]

  val handledError: ErrorOr[Int] = eitherME.handleError(failure) {
    case "Badness" => 42
    case _         => 89
  }

  val handleError2: ErrorOr[Int] = eitherME.handleErrorWith(failure) {
    case "Badness" => eitherME.pure(42)
    case _         => Left("Something else")
  }

  val filteredSuccess = eitherME.ensure(success)("Number too small")(_ > 100)

  // Try and Future
  val exception = new RuntimeException("Really bad")
  val pureException: Try[Int] =
    MonadError[Try, Throwable].raiseError(exception) // Failure(exception)

  val ec: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))

  // import cats.instances.future._
  //
  // MonadError[Future, Throwable].raiseError(
  //   exception
  // ) // Future which will complete with a Failure(exception)

  /** ApplicativeError */
  import cats.data.Validated

  type ErrorsOr[T] = Validated[String, T]

  import cats.instances.list._
  // val applicativeErrorVal = ApplicativeError[ErrorsOr, List[String]]

  /** Extension Methods */
  import cats.syntax._
  import cats.syntax.applicative._
  import cats.syntax.applicativeError._

  val extendedSucess = 42.pure[ErrorsOr]
  // val extendedError = List("Badness").raiseError[ErrorsOr, Int]

  // val recoverError: ErrorsOr[Int] = extendedError.recover { case _ =>
  //   43
  // }

  import cats.syntax.monadError._
  val testedSuccess = success.ensure("Something bad")(_ > 100)

}
