package io.javt.pfp.workshops.practical.lambdaconf2017.l2typeclasses

object L01_Typeclasses {

  trait Show[A]{
    def show(a: A): String
  }

  object Show {
    def apply[A: Show]: Show[A] = implicitly[Show[A]]

    implicit class ShowOps[A: Show](a: A) {
      def show: String = Show[A].show(a)
    }
  }

  class User(val firstName: String, val lastName: String)

  def hello(user: User): String = "Hello " + user

  def hello2[A](a: A): String = "Hello " + a

  def hello3[A](a: A)(sh: Show[A]): String = "Hello " + sh.show(a)

  def hello4[A](a: A)(implicit sh: Show[A]): String = "Hello " + sh.show(a)
  def hello5[A: Show](a: A): String = "Hello " + implicitly[Show[A]].show(a)
  def hello6[A: Show](a: A): String = "Hello " + Show.apply[A].show(a)
  def hello7[A: Show](a: A): String = "Hello " + Show[A].show(a)
  def hello9[A: Show](a: A): String = {
    import Show._
    "Hello " + a.show
  }

  def main(args: Array[String]): Unit = {
    val user = new User("Andres", "The ScalaMan")
    println(hello(user))
    println(hello2(user))

    val userShow: Show[User] = new Show[User] {
      def show(user: User): String = user.firstName + " " + user.lastName
    }

    println(hello3(user)(userShow))

    implicit val userShowImplicit: Show[User] = new Show[User] {
      def show(user: User): String = user.firstName + " " + user.lastName
    }

    println(hello4(user))
  }

}

