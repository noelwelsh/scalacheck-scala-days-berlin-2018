# Anatomy of ScalaCheck

Now that we have some practice describing properties, let's see how we
encode and run them using ScalaCheck.

## Properties: Asserting Invariants

For the example of the *invertibility* of addition--for every `x` there is an
integer `-x` such that `x + -x = 0`--we are expressing a *universally
quantified* property: the assertion should be true for *all*
`Int` values.

In ScalaCheck, we express universally quantified properties with the
`Prop.forAll` method:

```tut:book
import org.scalacheck.Prop.forAll

val invertability = forAll { (x: Int) => x + -x == 0 }
```

(We often `import` the `Prop.forAll` method directly into scope, for brevity.)

The function `forAll` is a higher-order function that transforms another
function that makes an assertion into a `Prop`, ScalaCheck's representation of
a property. Here we passed an anonymous function that takes an `Int` named
`x`, and it asserts that adding `x` to its inverse should always equal zero.

Let's reformat and annotate the expression to highlight its parts:

```tut:silent:book
val invertability =     // the property
  forAll { (x: Int) =>  // universal quantification over a type
    x + -x == 0         // the assertion
  }
```

Let's test our property:

```tut:book
import org.scalacheck._

Test.check(Test.Parameters.default, invertability)
```

(You can use `Test.check` in a REPL; later we will detail the usual practice
of running the property tests via `sbt`)

*what actually happened*
*fields of a `Result`*

## Generators: Producing Values to Test

When we create a `Prop` using `forAll`, we give it our function that
asserts the invariant on its input. Where does this input value come from?
These inputs come from *generators*, named `Gen` in ScalaCheck:

```scala
// (edited for brevity)
sealed abstract class Gen[+T] {
  def sample: Option[T]
}
```

At its heart, `Gen` produces sample values of a given type.

*why is the return value of `sample` wrapped in an `Option`?*

ScalaCheck comes with many, many generators out-of-the-box:

```tut:silent:book
Gen.alphaStr     // `String` values like "abc", etc.
Gen.numStr       // numeric `String` values: "123", etc.
Gen.posNum[Int]  // positive numbers: 42, etc.

Gen.choose(0, 9)         // pick any number in a range, inclusive
Gen.oneOf('a', 'b', 'c') // pick any of these values
```

There are many more.

Let's `sample` some values from these generators:

```tut:book
List.fill(3)(Gen.alphaStr.sample)
List.fill(3)(Gen.numStr.sample)
List.fill(3)(Gen.posNum[Int].sample)
List.fill(3)(Gen.choose(0, 9).sample)
List.fill(3)(Gen.oneOf('a', 'b', 'c').sample)
```

## Generator combinators

```tut:book
// generate a 'b' twice as much as an 'a', a 'c' three times as much
List.fill(3)(Gen.frequency(1 -> 'a', 2 -> 'b', 3 -> 'c').sample)

// generate a container whose elements come from another `Gen`
List.fill(3)(Gen.containerOf[List, Int](Gen.oneOf(1, 3, 5)).sample)

// generate a fixed-size container whose elements come from another `Gen`
List.fill(3)(Gen.containerOfN[Seq, String](3, Gen.alphaStr).sample)
```

## Generator composition

## Arbitrary

### Converting Arbitrary <=> Gen

## Examples

*database?*
