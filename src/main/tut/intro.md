# Essential Testing: Introduction

## A Traditional Unit Test

Here we have a traditional unit test:

```tut:silent
import org.scalatest._

class TraditionalTest extends FunSuite {
  test("String.startsWith should find prefixes") {
    assert("able was i ere i saw elba" startsWith "able")
  }

  test("String.endsWith should find suffixes") {
    assert("able was i ere i saw elba" endsWith "elba")
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
    } assert(string startsWith prefix)
  }

  test("String.endsWith should find suffixes") {
    for {
      (string, _, suffix) <- strings
    } assert(string endsWith suffix)
  }
}
```

This is better: we've separated our test inputs from our assertions.

- Fixture limitations
  - human generated
  - cherry-picked values, magic numbers, etc.