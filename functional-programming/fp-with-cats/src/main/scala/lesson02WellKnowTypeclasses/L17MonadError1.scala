package lesson02WellKnowTypeclasses

import cats._
import cats.implicits._
import java.io.IOException
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object L17MonadError1 extends App {

  trait HttpMethod
  case object GET extends HttpMethod

  case class HttpRequest(method: HttpMethod, url: String)
  case class HttpResponse(status: Int)

  def doRequest(req: HttpRequest): HttpResponse =
    if (math.random() < 0.5) throw new IOException("boom!")
    else HttpResponse(200)

  def executeRequest(req: HttpRequest): Option[HttpResponse] =
    try {
      Some(doRequest(req))
    } catch {
      case _: Exception => None
    }

  def executeRequest2(req: HttpRequest): Either[String, HttpResponse] =
    try {
      Right(doRequest(req))
    } catch {
      case _: Exception => Left("Sorry :(")
    }

  def executeRequest3(req: HttpRequest): Try[HttpResponse] =
    try {
      Success(doRequest(req))
    } catch {
      case e: Exception => Failure(e)
    }

  println("Execute single methods with no context ***********")
  (1 to 3).foreach { _ =>
    println(executeRequest(HttpRequest(GET, "www.example.com")))
    println(executeRequest2(HttpRequest(GET, "www.example.com")))
    println(executeRequest3(HttpRequest(GET, "www.example.com")))
    println()
  }

  /** Option MonadError */
  val optionME: MonadError[Option, Unit] = new MonadError[Option, Unit] {
    def pure[A](x: A): Option[A] = Some(x)

    def raiseError[A](e: Unit): Option[A] = None

    def handleErrorWith[A](fa: Option[A])(f: Unit => Option[A]): Option[A] =
      // fa match {
      //   case None    => f(())
      //   case Some(a) => Some(a)
      // }
      fa.orElse(f(()))

    def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] =
      //   fa match {
      //   case None    => None
      //   case Some(a) => f(a)
      // }
      fa.flatMap(f)

    def tailRecM[A, B](a: A)(f: A => Option[Either[A, B]]): Option[B] = ???

  }

  /** Either MonadError */
  def eitherME[E]: MonadError[Either[E, *], E] =
    new MonadError[Either[E, *], E] {
      def pure[A](x: A): Either[E, A] = Right(x)

      def raiseError[A](e: E): Either[E, A] = Left(e)

      def handleErrorWith[A](fa: Either[E, A])(
          f: E => Either[E, A]
      ): Either[E, A] = fa match {
        case Left(e)  => f(e)
        case Right(a) => Right(a)
      }

      def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] =
        fa.flatMap(f)

      def tailRecM[A, B](a: A)(f: A => Either[E, Either[A, B]]): Either[E, B] =
        ???

    }

  /** Try MonadError */
  val tryMe: MonadError[Try, Throwable] = new MonadError[Try, Throwable] {
    def pure[A](x: A): Try[A] = Success(x)

    def raiseError[A](e: Throwable): Try[A] = Failure(e)

    def handleErrorWith[A](fa: Try[A])(f: Throwable => Try[A]): Try[A] =
      fa match {
        case Failure(t) => f(t)
        case Success(a) => Success(a)
      }

    def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)

    def tailRecM[A, B](a: A)(f: A => Try[Either[A, B]]): Try[B] = ???

  }

  /** executeRequest using MonadError as constraint */

  def executeRequestME[F[_], E](
      req: HttpRequest
  )(f: Exception => E)(implicit me: MonadError[F, E]): F[HttpResponse] =
    try {
      me.pure(doRequest(req))
    } catch {
      case e: Exception => me.raiseError(f(e))
    }

  println("With Context ************")
  (1 to 3).foreach { _ =>
    // println(executeRequestME[Try](HttpRequest(GET, "www.example.com")))
    println(
      executeRequestME[Either[String, *], String](
        HttpRequest(GET, "www.example.com")
      )(e => e.getMessage)
    )
    println(
      executeRequestME[Option, Unit](HttpRequest(GET, "www.example.com"))(_ =>
        ()
      )
    )
    println()
  }

  println("MonadError Methods *************************")

  println("attempt")
  println(MonadError[Option, Unit].attempt(Some(5)))
  println(MonadError[Option, Unit].attempt(None))
  println()
  println(MonadError[Try, Throwable].attempt(Success(10)))
  println(
    MonadError[Try, Throwable].attempt(Failure(new Exception("boom!")))
  )
  println()

  println("ensure")
  println(MonadError[Option, Unit].ensure(Some(3))(())(_ % 2 == 0))
  println(MonadError[Option, Unit].ensure(Some(4))(())(_ % 2 == 0))
  println(
    MonadError[Either[String, *], String].ensure(Right(4))("oh no!!!")(
      _ % 2 == 0
    )
  )
  println(
    MonadError[Either[String, *], String].ensure(Right(5))("oh no!!!")(
      _ % 2 == 0
    )
  )

}
