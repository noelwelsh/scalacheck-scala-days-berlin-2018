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

class CorrectTodoSpec extends TodoSpec("Correct", new TodoAlgebra.InMemoryTodo)

// NOTE: try with different `TodoAlgebra.InMemoryTodo.Bug` values, not just all of them.
class BuggyTodoSpec extends TodoSpec(
  "Buggy",
  new TodoAlgebra.InMemoryTodo.WithBugs(
    List(TodoAlgebra.InMemoryTodo.Bug.FindReturnsWrongItem)
    // TodoAlgebra.InMemoryTodo.Bug.values.toList
  ))

abstract class TodoSpec[Item : Encoder](name: String, alg: => TodoAlgebra.Aux[IO, Item]) extends Properties(s"TodoService.$name") {

  import TodoRequest._

  property("GET /todos returns 200") =
    run(TodoRequest.GetTodos.toRequest)
      .runEmptyA(newService)
      .value
      .status == Status.Ok

  property("GET /todos returns JSON []") =
    run(TodoRequest.GetTodos.toRequest)
      .runEmptyA(newService)
      .value
      .as[Json].unsafeRunSync() == Json.arr()

  property("read your writes") =
    forAll(genPostTodo) { (post: TodoRequest.PostTodo) =>
      val r =
        for {
          postResponse <- run(post.toRequest)

          location = postResponse.headers.get(headers.Location).get.uri // TODO: don't just blow up

          // TODO: assert that returned Location matches the /todos/{id} endpoint, maybe URI Template thing

          // actually fetch the content at the Location URI to test the response
          getResponse <- run(Request[IO](Method.GET, location))
        } yield getResponse

      val (log, _, getResponse) = r.runEmpty(newService).value
      val entity = getResponse.as[TodoRequest.PostTodo].attempt.unsafeRunSync()

      val statusIsOk = s"getResponse.status: ${getResponse.status} != ${Status.Ok}" |: getResponse.status == Status.Ok
      val readEntityMatchesWritten = s"entity: $entity != ${Right(post)}" |: entity == Right(post)

      Log.show(log) |: statusIsOk && readEntityMatchesWritten
    }

  // TODO: DELETE /todos/{id}

  def newService() = new TodoService(alg).service

  /** Computation that requires a `HttpService` and also logs the request and response.
    * We keep no other state (it is type `Unit`). */
  type Http4sTest[F[_], A] = ReaderWriterState[HttpService[F], Log[F], Unit, A]

  /** We log the request/response pairs. */
  type Log[F[_]] = List[(Request[F], Response[F])]

  object Log {
    // Pretty-print the log.
    def show[F[_]](log: Log[F]): String =
      log.flatMap { case (req, res) => List(s">>> $req", s"<<< $res") }
        .mkString("\n")
  }

  def run(request: Request[IO]): Http4sTest[IO, Response[IO]] =
    for {
      service <- ReaderWriterState.ask[HttpService[IO], Log[IO], Unit]
      response = service.orNotFound(request).unsafeRunSync()
      _ <- ReaderWriterState.tell(List(request -> response))
    } yield response

  val genPostTodo: Gen[TodoRequest.PostTodo] =
    for {
      value <- Gen.alphaStr
      due <- Gen.option(Gen.choose(0L, 365L * 50) map (LocalDate.ofEpochDay))
    } yield TodoRequest.PostTodo(value, due)
}

/** Algebraic data type representing the various kinds of requests we can make to a `TodoService`. */
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