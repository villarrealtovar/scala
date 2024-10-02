package lesson02WellKnowTypeclasses

import cats.MonadThrow
import cats.effect.IO
import cats.syntax.all._
import scala.util._

object L17MonadError3 extends App {
  type F[A] = Either[Throwable, A]

  val myMonadThrow = MonadThrow[F]

  def f[T](a: T): F[T] = myMonadThrow.pure(a)

  /**   1. Recover from failure
    *
    * Methods in this category allow you to transform a failed value into a
    * success value, they are all ignored if the value is already a success
    * (with an exception for redeem, redeemWith, and option). All methods that
    * expect a function that yields value wrapped into F can be used for
    * selective recovery or error transformation as the function can return
    * either failure or success value.
    */

  // 1.1 OrElse
  // Replaces error regardless of its error type with the given value. The provided value
  // has to be wrapped into F and can be a failure as well, so it can be used to replace
  // an existing error with the given one.
  myMonadThrow.raiseError(new RuntimeException).orElse(f(42)) // Right(42)

  myMonadThrow
    .raiseError(new RuntimeException)
    .orElse(myMonadThrow.raiseError(new Exception)) //  Left(java.lang.Exception)

  // 1.2 forceR
  // Replaces failure regardless of the error type with a success value of an arbitrary
  // type. The provided value has to be wrapped into F. This method is provided by the
  // Cats Effect type class MonadCancel, so it’s available only for effect types like IO.
  IO.raiseError[Int](new RuntimeException).forceR(IO("a")) // IO(...)

  // 1.3 handleError
  // Allows you to map an error into a success value of the same type.
  myMonadThrow
    .raiseError[String](new RuntimeException("Error!"))
    .handleError(error => error.getMessage) // Right(Error!)

  // 1.4 handleErrorWith
  // The “F” counterpart of handleError, the value returned by the mapping
  // function has to be wrapped into F (can be a failure or a success).
  myMonadThrow
    .raiseError[String](new RuntimeException("Error!"))
    .handleErrorWith(error => f(error.getMessage)) // Right(Error!)

  // 1.5 redeem
  // Redeem combines handleError and map into one operation, you have to
  // provide two mapping functions, one of them will be evaluated depending on whether
  // the value is a success or a failure, the recovery value can be of an arbitrary type.
  f(1).redeem(
    error => error.getMessage,
    value => s"Success: [$value]"
  ) // Right(Success: [1])

  // 1.6 redeemWith
  // The “F” counterpart of redeem, a value returned by both mapping functions has to
  // be wrapped into F (can be a failure or a success).
  myMonadThrow
    .raiseError(new Exception("Error!"))
    .redeemWith(
      error =>
        myMonadThrow.raiseError[String](new RuntimeException(error.getMessage)),
      (value: String) => f(s"Success: [$value]")
    ) // Left(java.lang.RuntimeException: Error!)

  // 1.7 recover
  // Recover expects partial function enabling convenient selective recovery using
  // pattern matching. Failure values not matching any clause will remain a failure.
  case class MyException(value: String) extends Exception

  myMonadThrow.raiseError[String](MyException("a value")).recover {
    case MyException(value) => value
  } // res15: Either[Throwable,String] = Right(a value)

  myMonadThrow.raiseError[String](new RuntimeException).recover {
    case MyException(value) => value
  } //  res16: Either[Throwable,String] = Left(java.lang.RuntimeException)

  // 1.8 recoverWith
  // The “F” counterpart of recover, a value returned by the mapping function has to
  // be wrapped into F (can be a failure or a success).
  myMonadThrow.raiseError[String](MyException("a value")).recoverWith {
    case MyException(value) => f(value)
  } //  res2: Either[Throwable,String] = Right(a value)

  // 1.9 option
  // Converts failed IO into IO(None) and success IO(a) into IO(Some(a)).
  // This method is not provided by a type class, it is implemented directly by
  // the IO class, so it is available only for IO.
  IO.raiseError[Int](new Exception)
    .option // res23: cats.effect.IO[Option[Int]] = IO(...)

  /** 2. Recover from error by shifting the error to the value and back
    *
    * Methods in this category affect failure and success values, they allow
    * conversion from either success or failure into a success of Either type.
    */

  // 2.1 attempt
  // Converts success value F(a) into F(Right(a)) and failure into F(Left(error)),
  // the result is always a success value.
  f(1).attempt // res18: F[Either[Throwable,Int]] = Right(Right(1))

  myMonadThrow
    .raiseError[Int](new Exception)
    .attempt // res19: F[Either[Throwable,Int]] = Right(Left(java.lang.Exception)

  // 2.2. attemptT
  // Similar to attempt, but returns a value wrapped into EitherT monad transformer.
  myMonadThrow
    .raiseError[Int](new Exception)
    .attemptT // res20: cats.data.EitherT[F,Throwable,Int] = EitherT(Right(Left(java.lang.Exception)))

  // 2.3 attemptNarrow
  // Similar to attempt, but returns a success only if the error is of a provided type,
  // otherwise, it returns unaffected failure value.
  myMonadThrow
    .raiseError[Int](new Exception)
    .attemptNarrow[
      RuntimeException
    ] // res21: F[Either[RuntimeException,Int]] = Left(java.lang.Exception)

  myMonadThrow
    .raiseError[Int](new RuntimeException)
    .attemptNarrow[RuntimeException] // res22: F[Either[RuntimeException,Int]] = Right(Left(java.lang.RuntimeException))

  // 2.4 rethrow
  // The reverse of attempt converts a successful value of Either into a success or
  // failure value depending on the input Either being Left or Right.
  f(
    (new Exception).asLeft[Int]
  ).rethrow // res24: F[Int] = Left(java.lang.Exception)

  f(1.asRight).rethrow // res25: F[Int] = Right(1)

  /** 3. Turn success into failure if the value is invalid
    *
    * Methods in this category are used for validation, in the case of
    * unsuccessful validation, the value is turned into a failure. For failure
    * values, these methods are ignored.
    */

  // 3.1 ensure
  // Check if a value satisfies the given condition, if not, then fail with the given error.
  f(1).ensure(new RuntimeException)(a =>
    a > 0
  ) // res26: Either[Throwable,Int] = Right(1)

  f(-1).ensure(new RuntimeException)(a =>
    a > 0
  ) // res27: Either[Throwable,Int] = Left(java.lang.RuntimeException)

  // 3.2 ensureOr
  // Similar to ensure but allows to map the current value to an error.
  f(-1).ensureOr(a => new RuntimeException(s"Expected value > 0, got $a"))(a =>
    a > 0
  ) // res28: Either[Throwable,Int] = Left(java.lang.RuntimeException: Expected value >0, got: -1)

  // 3.3 reject
  // Reject expects partial function enabling convenient selective rejection using pattern matching.
  // Success values not matching any clause will remain a success.
  "a".asRight[Throwable].reject { case "b" =>
    new RuntimeException
  } // res45: Either[Throwable,String] = Right(a)

  /** 4. Transform error
    *
    * Methods in this category are used to translate an error of failure value,
    * they are ignored for success values.
    */

  // 4.1 orRaise
  // Replaces any error with the given error, see also orElse if you want to replace with an F wrapped value.
  myMonadThrow
    .raiseError[Int](new Exception)
    .orRaise(
      new RuntimeException
    ) // res31: F[Int] = Left(java.lang.RuntimeException)

  // 4.2 adaptError adaptErr
  // Expects partial function enabling convenient selective error transformation using pattern matching.
  // Unmatched errors remain untouched. Both methods do the same, the difference is that adaptErr is provided
  // by ApplicativeError type class and adapError by MonadError, in most cases, it doesn’t matter which you choose.
  myMonadThrow
    .raiseError[Int](new RuntimeException("Error message"))
    .adaptError { case e: RuntimeException =>
      new Exception(e.getMessage)
    } // res32: F[Int] = Left(java.lang.Exception: Error message)

  /** 5. Create failure value
    *
    * Methods in this category are used to create a failure value. The most
    * universal way to use them is to use a MonadError (or MonadThrow) type
    * class instance. With that instance, you can create a failure value in the
    * same way regardless of the actual type used to represent a value. You can,
    * of course, still use methods provided by specific types like Left(error)
    * for Either or Failure(error) for Try.
    */

  // 5.1 raiseError
  // Creates failed value from the given error. Can be called either on an instance
  // of ApplicativeError (or its descendant) type class or an instance of an error or the IO object.
  // Returns value of an arbitrary type, sometimes you may need to provide the type of wrapped value if
  // the compiler can’t infer it.
  myMonadThrow.raiseError[Int](
    new RuntimeException
  ) // F[Int] = Left(java.lang.RuntimeException

  (new RuntimeException)
    .raiseError[F, Int] // F[Int] = Left(java.lang.RuntimeException)

  IO.raiseError[Int](
    new RuntimeException
  ) // res35: cats.effect.IO[Int] = IO(...)

  // 5.2 raiseWhen raiseUnless
  // Creates a failed value when a condition is true (when) or false (unless), otherwise returns Unit wrapped
  // into F, the return type is always F[Unit]. Can be called either on an instance of ApplicativeError
  // (or its descendant) or the IO object.
  myMonadThrow.raiseWhen(false)(
    new RuntimeException
  ) // res37: F[Unit] = Right(())

  myMonadThrow.raiseUnless(false)(
    new RuntimeException
  ) // res38: F[Unit] = Left(java.lang.RuntimeException)

  IO.raiseWhen(true)(
    new RuntimeException
  ) // res39: cats.effect.IO[Unit] = IO(...)

  IO.raiseUnless(true)(
    new RuntimeException
  ) //  res40: cats.effect.IO[Unit] = IO(())

  /** 6. Catch exceptions
    *
    * Methods from this category are used to interface with imperative code that
    * can throw an exception, they allow to convert the result of an expression
    * into a success or failure value (if an exception was thrown).
    */

  // 6.1 catchNonFatal
  // Catches all (non-fatal) exceptions. Can be called on an instance of ApplicativeError (or its descendant)
  // or on the Either object (after importing either syntax from Scala Cats).
  myMonadThrow.catchNonFatal(
    sys.error("Error!")
  ) // res41: F[Nothing] = Left(java.lang.RuntimeException: Error!)

  Either.catchNonFatal(
    sys.error("Error!")
  ) // res54: Either[Throwable,Nothing] = Left(java.lang.RuntimeException: Error!)

  // 6.2 catchNonFatalEval
  // Similar to catchNonFatal, but the expression has to be wrapped into Eval.
  myMonadThrow.catchNonFatalEval(
    cats.Eval.defer(sys.error("Error!"))
  ) // res42: F[Nothing] = Left(java.lang.RuntimeException: Error!)

  // 6.3 catchOnly
  // Similar to catchNonFatal, but catches only selected exception (and its descendants). Warning, other exceptions are being rethrown!

  myMonadThrow.catchOnly[RuntimeException](
    throw new Exception
  ) // java.lang.Exception at $anonfun$res58$1(<console>:1) at cats.ApplicativeError$CatchOnlyPartiallyApplied$.apply$extension(ApplicativeError.scala:337) ... 35 elided

  /** 7. Execute a side effect logging
    *
    * Methods in this category are not actually used for error handling,
    * probably the most common case is logging an error, they usually leave the
    * original value unaffected.
    */

  // 7.1 attemptTap
  // Calls the side effect passing result of the attempt method called on the original value. The side effect is called for
  // success and failure values. The original value stays unaffected unless the side effect returns a failure, then the result
  // is a failure with the error returned by the side effect.
  f(1).attemptTap(errorEitherValue =>
    f(println(s"log: $errorEitherValue"))
  ) // log: Right(1); val res43: F[Int] = Right(1)

  myMonadThrow
    .raiseError[Int](new Exception)
    .attemptTap(errorEitherValue =>
      f(println(s"log: $errorEitherValue"))
    ) // log: Left(java.lang.Exception); val res44: F[Int] = Left(java.lang.Exception)

  f(1).attemptTap(errorEitherValue =>
    myMonadThrow.raiseError(new Exception("Side effect error"))
  ) // res5: F[Int] = Left(java.lang.Exception: Side effect error)

  // 7.2 onError
  // Calls the side effect only for a failure value, passing error as the parameter, if the side effect returns a failure value,
  // the error of the original value is replaced with the error of the side effect (with the exception for IO, which preserves the original error).
  myMonadThrow
    .raiseError[Int](new Exception)
    .onError(errorEitherValue =>
      f(println(s"log: $errorEitherValue"))
    ) // log: java.lang.Exception; val res3: F[Int] = Left(java.lang.Exception)

  f(1).onError(eitherErrorValue =>
    myMonadThrow.raiseError(new Exception)
  ) // res45: F[Int] = Right(1)
}
