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

  implicit def encoder: Encoder[Item]

  /** Construct an `Item`. */
  def item(value: String, due: Option[LocalDate]): Item

  /** Append an `Item` to the list. */
  def append(item: Item): F[ItemId]

  /** Get all the `Item`s in the list. */
  def getItems(): F[List[Item]]

  /** Find an `Item` by its `ItemId`. */
  def find(id: ItemId): F[Option[Item]]
}

object TodoAlgebra {

  case class Item(value: String, due: Option[LocalDate])

  class InMemoryTodo[F[_] : Applicative] extends TodoAlgebra[F] {

    type Item = TodoAlgebra.Item

    implicit def encoder: Encoder[Item] = deriveEncoder[Item]

    private var items: List[Item] = List.empty

    def item(value: String, due: Option[LocalDate]): Item =
      Item(value, due)

    def append(item: Item): F[ItemId] =
      Applicative[F].pure {
        items = items :+ item
        items.length.toLong
      }

    def getItems(): F[List[Item]] =
      Applicative[F].pure(items)

    def find(id: ItemId): F[Option[Item]] =
      Applicative[F].pure(items.lift(id.toLong.toInt - 1))
  }
}

object Bugs {
  class ReturnsWrongLocation[F[_] : Applicative] extends TodoAlgebra.InMemoryTodo[F] {
    override def append(item: Item): F[ItemId] =
      super.append(item) map (_ + 5)
  }
}

