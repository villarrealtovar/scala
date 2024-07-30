package io.javt.pfp.workshops.practical.lambdaconf2017.l1intro

import cats.syntax.all._
import cats.effect._

object L03_Misc {

  /** algebraic data types [ADT] */

  sealed trait Figure
  case class Rectangle(width: Int, height: Int) extends Figure
  case class Cicrle(radius: Int) extends Figure

  sealed trait Expression
  case class Sum(exp1: Expression, exp2: Expression) extends Expression
  case class Multiply(exp1: Expression, exp2: Expression) extends Expression
  case class IntValue(v: Int) extends Expression

  /** Option */
  def calculateRisk(input: String): Option[Int] = input match {
    case "blah" => None
    case a => Some(a.length)
  }

  /** Either (Disjunction) */
  def calculateRisk2(input: String): Either[String, Int] = input match {
    case "blah" => "Wel... blah happend...".asLeft
    case a => a.length.asRight
  }

  /** Task */
  case class User(id: Int, login: String)

  def fetchUser(id: Int): IO[User] = IO.delay {
    // real jdbc calls here
    User(id, s"login$id")
  }

  val user: IO[User] = fetchUser(10)
  user.unsafeRunSync()

  case class DBException() extends RuntimeException

  def fetchUserException(id: Int): IO[User] = IO.delay {
    throw new DBException
  }

  val userWithException: IO[Either[Throwable, User]] = fetchUserException(10).attempt

  val login: IO[String] = fetchUser(10).map(_.login)
  val login2: IO[String]  = login.handleError {
    case DBException() => "not found"

  }

}
