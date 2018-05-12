package io.underscore.testing.todo

import cats._
import cats.data._
import cats.effect.IO
import cats.implicits._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.java8.time._
import io.circe.syntax._
import java.time.LocalDate
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalacheck._
import org.scalacheck.Prop._

class TodoSpec extends Properties("TodoService") {

  import TodoRequest._

  property("GET /todos returns 200") =
    run(TodoRequest.GetTodos.toRequest)(newService).status == Status.Ok

  property("GET /todos returns JSON []") =
    run(TodoRequest.GetTodos.toRequest)(newService).as[Json].unsafeRunSync() == Json.arr()

  property("read your writes") =
    forAll(genPostTodo) { (post: TodoRequest.PostTodo) =>
      // TODO: maybe Writer to collect debug info
      val r: Reader[HttpService[IO], (Response[IO], Uri, Response[IO])] =
        for {
          postResponse <- run(post.toRequest)
          // TODO: don't just blow up
          location = postResponse.headers.get(headers.Location).get.uri
          // TODO: assert that returned Location matches the /todos/{id} endpoint, maybe URI Template thing

          // actually fetch the content at the Location URI to test the response
          getResponse <- run(Request[IO](Method.GET, location))
        } yield (postResponse, location, getResponse)

      val (postResponse, location, getResponse) = r.run(newService)
      s""">>> $post
         |<<< $postResponse
         |>>> $location
         |<<< $getResponse""".stripMargin |:
        getResponse.status == Status.Ok &&
        getResponse.as[TodoRequest.PostTodo].unsafeRunSync() == post
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

  implicit def postDecoder: Decoder[PostTodo] = deriveDecoder[PostTodo]
  implicit val postEntityDecoder: EntityDecoder[IO, PostTodo] = jsonOf[IO, TodoRequest.PostTodo]
}