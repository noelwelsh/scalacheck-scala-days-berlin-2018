# Writing Programs That Write Tests: Better Testing With ScalaCheck

Target audience level: Intermediate

Writing tests is so... boring. And repetitive. And often, in hindsight, rather ad hoc, and therefore ineffective. In this workshop we'll learn about property-based testing using ScalaCheck: how it compares to unit testing, when is it better (or worse); learning about different families of invariants and when they are appropriate for your code; how to structure the tests using the main ScalaCheck abstractions, run them, and debug them; and how to test stateful systems. We will write lots of code! And your code will write lots of tests!

## A Traditional Unit Test

Here we have a traditional unit test:

```tut:silent
import org.scalatest._

class TraditionalTest extends FunSuite {
  test("String.startsWith should find prefixes") {
    assert("able was i ere i saw elba".startsWith("able"))
  }

  test("String.endsWith should find suffixes") {
    assert("able was i ere i saw elba".endsWith("elba"))
  }
}
```

TODO: what's bad about it

Let's extract the test inputs into a "fixture":

```tut:silent
trait Fixture {
  val strings =
    ("able was i ere i saw elba",     "able",    "elba")  ::
    ("connect the dots, la la la la", "connect", "la la") ::
    Nil
}

class TraditionalTestWithFixture extends FunSuite with Fixture {
  test("String.startsWith should find prefixes") {
    for {
      (string, prefix, _) <- strings
    } assert(string.startsWith(prefix))
  }

  test("String.endsWith should find suffixes") {
    for {
      (string, _, suffix) <- strings
    } assert(string.endsWith(suffix))
  }
}
```

This is better: we've separated our test inputs from our assertions.
