package io.underscore.testing.todo

import cats.effect.Effect
import io.circe.Json
import java.time.LocalDate
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import scala.util.Try

class TodoService[F[_]: Effect] extends Http4sDsl[F] {

  val service: HttpService[F] = {
    HttpService[F] {
      case req @ POST -> Root / "todos" =>
        req.decode[UrlForm] { data =>
          (data.values.get("value"), data.values.get("due")) match {
            case (Some(Seq(value, _*)), Some(Seq(DueMatcher(due), _*))) =>
              // TODO: actually store item
              Created(Json.fromString(s"/todos/1: value=$value, due=$due"))

            case (Some(Seq(value, _*)), None) =>
              // TODO: actually store item
              Created(Json.fromString(s"/todos/1: value=$value"))

            case _ =>
              // TODO: Use Validated to accumulate errors.
              BadRequest()
          }
        }

      case GET -> Root / "todos" =>
        // TODO: really return items
        Ok(Json.arr())

      case GET -> Root / "todos" / LongVar(id) =>
        // TODO: really return item
        Ok(Json.obj("message" -> Json.fromString(s"Hello, ${id}")))
    }
  }

  object DueMatcher {
    def unapply(s: String): Option[LocalDate] =
      Try(LocalDate.parse(s)).toOption
  }
}
