package io.underscore.testing.todo

import cats._
import cats.implicits._
import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.java8.time._
import java.time.LocalDate

trait TodoAlgebra[F[_]] {

  type Item
  type ItemId = Long

  /** Construct an `Item`. */
  def item(value: String, due: Option[LocalDate]): Item

  /** Append an `Item` to the list. */
  def append(item: Item): F[ItemId]

  /** Find all the `Item`s in the list. */
  def findAll(): F[List[Item]]

  /** Find an `Item` by its `ItemId`. */
  def find(id: ItemId): F[Option[Item]]

  /** Complete an `Item` with a given `ItemId`. */
  def complete(id: ItemId): F[Unit]
}

object TodoAlgebra {

  type Aux[F[_], Item0] = TodoAlgebra[F] { type Item = Item0 }

  case class Item(value: String, due: Option[LocalDate])

  object Item {
    implicit def encoder: Encoder[Item] = deriveEncoder[Item]
  }

  class InMemoryTodo[F[_] : Applicative] extends TodoAlgebra[F] {

    type Item = TodoAlgebra.Item

    /* Non-complete items are `Some`, completed items are `None`,
     * so we don't break the Itemid <-> List index invariant. */
    private var items: List[Option[Item]] = List.empty

    def item(value: String, due: Option[LocalDate]): Item =
      Item(value, due)

    def append(item: Item): F[ItemId] =
      Applicative[F].pure {
        items = items :+ Some(item)
        items.length.toLong
      }

    def findAll(): F[List[Item]] =
      Applicative[F].pure(items collect { case Some(item) => item })

    def find(id: ItemId): F[Option[Item]] =
      Applicative[F].pure(items.lift(id.toLong.toInt - 1).flatten)

    def complete(id: ItemId): F[Unit] =
      Applicative[F].pure {
        val index = id.toLong.toInt - 1

        if (items.isDefinedAt(index)) items = items.updated(index, None)

        ()
      }
  }

  object InMemoryTodo {
    import enumeratum._

    sealed trait Bug extends EnumEntry

    object Bug extends Enum[Bug] {
      val values = findValues

      case object AppendReturnsWrongId extends Bug
      case object FindAlwaysFails extends Bug
      case object FindReturnsWrongItem extends Bug
    }

    class WithBugs[F[_] : Applicative](bugs: List[Bug]) extends InMemoryTodo[F] {

      def chaosBug[A](a: => A)(bug: Bug, f: A => A) =
        bugs.find(_ == bug).fold(a)(_ => f(a))

      override def append(item: Item): F[ItemId] =
        chaosBug(super.append(item))(Bug.AppendReturnsWrongId, _ map (_ + 5))

      override def find(id: ItemId): F[Option[Item]] = {
        val x = chaosBug(super.find(id))(Bug.FindReturnsWrongItem, _ map (_ map (_.copy(value = "ha ha ha!"))))
        chaosBug(x)(Bug.FindAlwaysFails, _ => Option.empty[Item].pure[F])
      }
    }
  }
}
