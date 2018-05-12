# Advanced ScalaCheck

In this section we discuss some issues that go beyond the basics of ScalaCheck usage.

## Discarding

We've seen how we can express preconditions by filtering a generator. This can have some issues. Consider the following example, creating a generating to produce strings beginning with "henry".

```tut:silent:book
import org.scalacheck._

val henry = Gen.alphaStr.filter(_.startsWith("henry"))
```

When we run a test using this generator we see that no tests were generated.

```tut:book
import org.scalacheck.Prop.forAll

val startsWithHenry = forAll(henry){ (s: String) => s.startsWith("henry") }

Test.check(Test.Parameters.default, startsWithHenry)
```

As you can see, the result tells us that ScalaCheck is `Exhausted`, meaning it couldn't generate any samples that met our preconditions. This is the danger of using preconditions---they aren't very efficient. It's more efficient to generate only valid samples.

## Implication

There is another way to specify preconditions in ScalaCheck, which we have not seen so far. Instead of specifying a precondition as a filter on a generator we can instead specify it on a property, using the "implication" operator `==>`. The following code shows these two alternate ways in a test for invertability of `Int`.

```tut:silent:book:
// Filter the generator
// In this simple case we could also write 
//  Gen.choose(Int.MinValue + 1, Int.MaxValue)
val nonMinValue: Gen[Int] = 
  Gen.choose(Int.MinValue, Int.MaxValue).suchThat(_ > Int.MinValue)

forAll(nonMinValue) { (x: Int) => x + -x == 0 }

// Alternate implementation putting the precondition on the property
//
// We need to import Prop._ to get into scope the implicit that adds the ==> syntax
import org.scalacheck.Prop._

forAll { (x: Int) => (x > Int.MinValue) ==> (x + -x == 0) }
```

Although using `==>` may be aesthetically pleasing, I find I don't use it in my tests. The reason being I often find I need to construct custom generators for my tests for other reasons (e.g. efficiency) and using just this single technique keeps the code base more consistent compared to switching between implication and custom generators on a case-by-case basis.



Shrinking and its stupidity. Turning it off.

Labels and other stupid symbolic operators

Generating recursive data. Bias. Unbiased generation?

Properties of different distributions?

Setup and teardown?

Reusing properties?

Organising tests?

`Gen`erating functions?
