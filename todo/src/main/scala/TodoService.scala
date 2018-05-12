package io.underscore.testing.todo

import cats.effect.Effect
import cats.implicits._
import io.circe.Json
import io.circe.syntax._
import java.time.LocalDate
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import scala.util.Try

class TodoService[F[_]: Effect](alg: TodoAlgebra[F]) extends Http4sDsl[F] {

  import alg._ // imports implicits we need

  val service: HttpService[F] = {
    HttpService[F] {
      case req @ POST -> Root / "todos" =>
        req.decode[UrlForm] { data =>
          (data.values.get("value"), data.values.get("due")) match {
            case (Some(Seq(value, _*)), Some(Seq(DueMatcher(due), _*))) =>
              for {
                id <- alg.append(alg.item(value, Some(due)))
                response <- Created(headers.Location(Uri.uri("/todos") / id.toString))
              } yield response.putHeaders()

            case (Some(Seq(value, _*)), None) =>
              for {
                id <- alg.append(alg.item(value, None))
                response <- Created(headers.Location(Uri.uri("/todos") / id.toString))
              } yield response

            case _ =>
              // TODO: Use Validated to accumulate errors.
              BadRequest()
          }
        }

      case GET -> Root / "todos" =>
        for {
          items <- alg.getItems
          response <- Ok(items.asJson)
        } yield response

      case GET -> Root / "todos" / LongVar(id) =>
        for {
          item <- alg.find(id)
          response <- item.fold(NotFound())(i => Ok(i.asJson))
        } yield response
    }
  }

  object DueMatcher {
    def unapply(s: String): Option[LocalDate] =
      Try(LocalDate.parse(s)).toOption
  }
}
