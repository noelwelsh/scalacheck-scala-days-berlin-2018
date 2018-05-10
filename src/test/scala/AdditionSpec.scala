import org.scalacheck._
import org.scalacheck.Prop.forAll

object AdditionSpecification extends Properties("Addition") {

  property("identity") = forAll { (x: Int) => x + 0 == 0 }

  property("associativity") = forAll { (x: Int, y: Int, z: Int) =>
    (x + y) + z == x + (y + z)
  }

  property("commutativity") = forAll { (x: Int, y: Int) =>
    x + y == y + x
  }

  property("invertibility") = forAll { (x: Int) => x + -x == 0 }
}
