# Exercise - Child Actor

## Objective

We previously implemented **Barista** and **CoffeeMachine** actors individually. In this exercise, we will start to build an actor hierarchy where actors work together.

- **Barista** will be the user guardian actor (instantiated along with the ActorSystem).
- **CoffeeMachine** will be spawned as a child of **Barista** with the actor name **coffee-machine**.

## Instructions

- Edit **Barista.scala** to spawn the **CoffeeMachine** actor at the creation of the Barista. The CoffeeMachine actor name should be **coffee-machine**.
- Add the required code to allow interaction with the CoffeeMachine child.

- There are unit tests that validate your solution. You can run the tests using sbt: `sbt clean test`.

## Reflection

- Notice what value is returned when spawning the child actor.
