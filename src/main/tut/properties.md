# Properties, Types, and Tests

What are properties? Where do properties come from?

Examples

- Addition (associative, commutative, invertable)
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
