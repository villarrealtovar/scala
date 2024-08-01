# Exercise - Barista Actor - Object-Oriented Style

## Objective

- **Barista** actor will use the object-oriented style:
  - He needs to keep track of orders and operate the coffee machine.
    His behavior (actions) depends on mutable state that he needs to keep track of (order statuses, coffee machine status).
    Object-oriented style is a good fit for this use case.

  - Barista actor is the entry point of our coffee house, he will be the guardian behavior.
    Note: In the real world, a coffee house can have more than one barista. We simplify the problem with a single barista working in the coffee house for the scope of this course.

## Instructions

- Complete the **Barista.scala** actor implementation to:

  - Barista keeps track of the orders using a mutable variable `private val orders: mutable.Map[String, Coffee]`
    - `String` type being the guest's name (who ordered the coffee).
    - `Coffee`, the type of coffee ordered.
  
  - When receiving `OrderCoffee(whom: String, coffee: Coffee)` message, Barista should:
    - Log the orders using `context.log.info()`.
      For example, if Lisa ordered an Akkaccino and Bart ordered a CaffeJava, the log message should be:
      `"Orders:[Lisa->Akkaccino,Bart->CaffeJava]"`
      (Actually preparing the coffee requires interaction with the coffee machine and will happen later in this course)

- Replace `???` in the body of the function with your implementation as part of the exercise.

- There are unit tests that validate your solution. You can run the tests using sbt: `sbt clean test`.

## Hints

- Look at the provided `def printOrders(orders: Set[(String, Coffee)]): String` function that helps to format the expected string.
- A `Map` can be converted to a `Set` with `.toSet`.
