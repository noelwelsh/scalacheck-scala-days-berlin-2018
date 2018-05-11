package io.underscore.testing.todo

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

// TODO: use scalatest
class TodoSpec extends org.specs2.mutable.Specification {

  "HelloWorld" >> {
    "return 200" >> {
      uriReturns200()
    }
    "return hello world" >> {
      uriReturnsHelloWorld()
    }
  }

  private[this] val retTodo: Response[IO] = {
    val getHW = Request[IO](Method.GET, Uri.uri("/hello/world"))
    new TodoService[IO].service.orNotFound(getHW).unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retTodo.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsHelloWorld(): MatchResult[String] =
    retTodo.as[String].unsafeRunSync() must beEqualTo("{\"message\":\"Hello, world\"}")
}
