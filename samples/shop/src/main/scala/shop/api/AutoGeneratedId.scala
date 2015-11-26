package shop.api

import akka.pattern._
import io.funcqrs.DomainEvent
import io.funcqrs.akka.AggregateActor.SuccessfulCommand
import play.api.libs.json.{ JsError, JsSuccess }
import play.api.mvc.Action

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AutoGeneratedId {
  this: CommandController =>

  def toLocation(event: DomainEvent): String

  def create = Action.async(parse.json) { request =>

    val createCmd = toCommand(request.body)

    createCmd match {
      case JsSuccess(cmd, _) =>
        (aggregateManager ? cmd)
          .mapTo[SuccessfulCommand]
          .map { result =>
            Created.withHeaders("Location" -> toLocation(result.events.head))
          }
      case e: JsError => Future.successful(BadRequest(JsError.toJson(e)))
    }

  }

}
