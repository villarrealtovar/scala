package io.javt.pfp.workshops.practical.lambdaconf2017.l4state

object Test {





    def main(args: Array[String]) {
      // println(Palindrome(scala.io.StdIn.readLine()));
      Palindrome("eye")
    }

    // code goes here
    def Palindrome(str: String): String = {

      def loop(s: List[Char], acc: Boolean): String = {
        s match {
          case Nil => acc.toString
          case x :: xs => {
            println(s, " ", x, " ",  xs.last)
            if (x == xs.last) loop(xs.init, acc) else false.toString()
          }
        }

      }


      loop(str.toList, true)
    }



}
