# Properties, Types, and Tests

The main task in property-based testing is coming up with the properties for the code under test. In this section we get some practice at this crucial step. We'll focus on fairly simple functions, and develop a library of properties we can reuse in more complicated situations.

## Integers

Let's start with a very familiar function: addition. To make it more interesting we'll consider first the integers we use in mathematics and then the `Int` type in Scala. What properties could you test in each case? Three properties is a good number, and with a bit of effort you might come up with more. Work on this before you read on.

For the mathematical integers, you might consider properties such as the following. In the below `x`, `y`, and `z` are always integers.

- *closure*: for any two integers `x` and `y`, `z = x + y` is also an integer
- *identity*: `x + 0 = x = 0 + x`
- *associativity*: `(x + y) + z = x + (y + z)`
- *commutativity*: `x + y = y + x`
- *invertibility*: if `z = x + y` there is an integer `-y` such that `z + -y = x`

These are the basic properties of addition. We could expand out to other properties, such as addition of odd and even numbers, if we were so inclined.

When we look at machine integers (i.e. `Int`) things get a bit more complex, due to overflow and wrapping. Let's consider the properties we developed for mathematical integers and see how they apply to machine integers.

- *closure*: still holds
- *identity*: still holds
- *associativity*: still holds
- *commutativity*: still holds
- *invertibility*: does not hold for `Int.MinValue` and does not hold when there is overflow

There are two big points we can take away from this exercise. 

The first is about the nature of properties. The properties given above will apply in many cases, and we can use the list above as a reference for discovering properties of other systems. More generally the properties are mostly kinds of *symmetries*---different arrangements of additions that lead to the same result. We can use this more general idea to find properties by looking for symmetries in our system under test.

The second big point is that sometimes properties have *preconditions*, as we saw for invertibility on `Int`. _More on preconditions here_

_Now write them as ScalaCheck specifications_

_Now more exercises_
- List append (associative and invertable)
- List length (inductive, non-negative)
- Sorting (idempotent)
- More here ...

Types and tests
- types prove for all input
  - but
    - expensive to prove all properties
    - compiler allows us to cheat: partial functions
- manual tests proves for a few input
  - but
    - we can prove complex properties we cannot express in types
- property based testing bridges the gap: complex properties with many more inputs than we could manually create
