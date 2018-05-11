package io.underscore.testing.todo

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalacheck._

// TODO: use scalatest
class TodoSpec extends Properties("TodoService") {

  private[this] val retTodo: Response[IO] = {
    val todos = Request[IO](Method.GET, Uri.uri("/todos"))
    new TodoService[IO].service.orNotFound(todos).unsafeRunSync()
  }

  property("/todos returns 200") =
    retTodo.status == Status.Ok

  property("/todos returns JSON []") =
    retTodo.as[Json].unsafeRunSync() == Json.arr()
}
