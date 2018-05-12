package io.underscore.testing.todo

import cats._
import cats.data._
import cats.effect.IO
import cats.implicits._
import io.circe.Json
import java.time.LocalDate
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalacheck._
import org.scalacheck.Prop._

class TodoSpec extends Properties("TodoService") {

  property("GET /todos returns 200") =
    run(TodoRequest.GetTodos.toRequest)(newService).status == Status.Ok

  property("GET /todos returns JSON []") =
    run(TodoRequest.GetTodos.toRequest)(newService).as[Json].unsafeRunSync() == Json.arr()

  property("read your writes") =
    forAll(genPostTodo) { (post: TodoRequest.PostTodo) =>
      val r =
        for {
          postResponse <- run(post.toRequest)
          location = postResponse.headers.get(headers.Location).get.uri
          getResponse <- run(Request[IO](Method.GET, location))
        } yield (postResponse, location, getResponse)

      val (postResponse, location, getResponse) = r.run(newService)
      s"$post\n$postResponse\n$location\n$getResponse" |: getResponse.status == Status.Ok
    }

  def newService() = new TodoService[IO](new TodoAlgebra.InMemoryTodo[IO]).service

  def run(request: Request[IO]): Reader[HttpService[IO], Response[IO]] =
    Reader(service => service.orNotFound(request).unsafeRunSync())

  val genPostTodo: Gen[TodoRequest.PostTodo] =
    for {
      value <- Gen.alphaStr
      due <- Gen.option(Gen.choose(0L, 365L * 50) map (LocalDate.ofEpochDay))
    } yield TodoRequest.PostTodo(value, due)
}

sealed trait TodoRequest {
  def toRequest: Request[IO] =
    this match {
      case TodoRequest.PostTodo(value, Some(due)) =>
        Request[IO](Method.POST, Uri.uri("/todos"))
          .withBody(UrlForm(Map("value" -> Seq(value), "due" -> Seq(due.toString))))
          .unsafeRunSync()

      case TodoRequest.PostTodo(value, None) =>
        Request[IO](Method.POST, Uri.uri("/todos"))
          .withBody(UrlForm(Map("value" -> Seq(value))))
          .unsafeRunSync()

      case TodoRequest.GetTodos =>
        Request[IO](Method.GET, Uri.uri("/todos"))

      case TodoRequest.GetTodo(id) =>
        Request[IO](Method.GET, Uri.uri("/todos") / id.toString)
    }
}

object TodoRequest {
  case class PostTodo(value: String, due: Option[LocalDate]) extends TodoRequest
  case object GetTodos extends TodoRequest
  case class GetTodo(id: Long) extends TodoRequest
}
