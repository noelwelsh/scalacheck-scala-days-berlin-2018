# Writing Programs That Write Tests: Better Testing With ScalaCheck

Target audience level: Intermediate

Writing tests is so... boring. And repetitive. And often, in hindsight, rather ad hoc, and therefore ineffective. In this workshop we'll learn about property-based testing using ScalaCheck: how it compares to unit testing, when is it better (or worse); learning about different families of invariants and when they are appropriate for your code; how to structure the tests using the main ScalaCheck abstractions, run them, and debug them; and how to test stateful systems. We will write lots of code! And your code will write lots of tests!

 - prerequisites
   - exercise repo + setup

 - intro
   - testing issues
   - testing styles
     - unit testing
     - integration testing
     - property-based testing
   - structure
     - assertions
     - SUT (System Under Test)
     - fixtures
      - limitations
        - human generated
        - cherry-picked values, magic numbers, etc.

 - property-based testing
  - The Point
    - Separate *invariant assertions* from *example generation*
    - Teach the computer how to generate examples (avoids cherry-picking)
  - Definitions
    - property: universally quantified boolean formula
    - generator: produces random values for quantified variables of properties
    - shrinking

 - properties
    - `∘` is pronounced "after"
    - identity
      - "some things never change"
      - `id ∘ f = f ∘ id = f`
      - e.g., appending `List.empty` gives you the same `List`
    - associativity
      - "different paths, same destination"
      - `h ∘ (g ∘ f) = (h ∘ g) ∘ f`
    - commutativity
      - "different paths, same destination"
      - `f ∘ g = g ∘ f`
      - e.g., independent updates can occur in any order
    - invertibility
      - "there and back again"
      - `f ∘ g = id`
      - maybe also g ∘ f = id, a.k.a isomorphism
      - e.g., read/write, addition/subtraction, insert/contains, create/exists
    - idempotency
      - "the more things change, the more they stay the same"
      - `f ∘ f = f`
      - e.g., `List.sorted`
   - induction: if property true for smaller part(s), then true for larger whole
      - height of tree is the max of the height of the branches
   - "hard to prove, easy to verify"
      - does the computed path solve the maze?
      - `concat(tokenize(string)) = string`
   - test oracle
      - new vs. old
      - optimized vs. brute force
      - parallel vs. serialized

 - generators
    - primitives
      - `Gen.alphaStr` // "abc"
      - `Gen.numStr` // "123"
      - `Gen.posNum` // 42
      - `Gen.choose(0, 9)`
      - `Gen.oneOf('a', 'b', 'c')`
  - combinators
    - `Gen.frequency(1 -> 'a', 2 -> 'b', 3 -> 'c')`
    - `Gen.containerOf[List, Int](Gen.oneOf(1, 3, 5))`
    - `Gen.containerOfN[Seq, String](8, Gen.alphaStr)`
  - composition
    ```tut:book
    import org.scalacheck._
    case class Foo(s: String, i: Int)

    for {
      s <- Gen.identifier
      i <- Gen.posNum[Int]
    } yield Foo(s, i)
    ```

 - stateful testing
   - problem: state is hidden from the outside
    - e.g., the user has to enter the correct PIN code before any money could be withdrawn from the ATM
    ```tut:book
    // authorized state is hidden from the outside!
    def withdraw(amount: Amount): Either[Unauthorized, Unit]
    ```tut
  - solution: model state explicitly in the test, check the state after (generated!) commands are run
  ```
   SUT      State
  -----    -------
    |         |
    v         v
    .....?.....
    |         |
    v         v
    .....?.....
    |         |
    v         v
    .....?.....
    |         |
    v         v
  ```
  - but don't forget, hidden state is evil!
   - exercise: Counter
   - exercise: ATM

 - Exercises
   - DiamondKata.scala
   - https://github.com/underscoreio/chatbot
