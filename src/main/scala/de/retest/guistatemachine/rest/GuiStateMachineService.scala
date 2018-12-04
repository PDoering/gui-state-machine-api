package de.retest.guistatemachine.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import de.retest.guistatemachine.api.{GuiStateMachine, GuiStateMachineApi, Id, State}
import de.retest.guistatemachine.rest.json.DefaultJsonFormats
import io.swagger.annotations.{Api, ApiImplicitParam, ApiOperation, ApiResponse, ApiResponses}
import javax.ws.rs.Path

@Api(value = "state-machine")
@Path("state-machine")
class GuiStateMachineService(api: GuiStateMachineApi) extends Directives with DefaultJsonFormats {

  def getRoute(): Route = getStateMachine() ~ deleteStateMachine() ~ postStateMachine() ~ getState()

  @Path("{id}")
  @ApiOperation(httpMethod = "GET", response = classOf[GuiStateMachine], value = "Returns a state machine based on the ID")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "State machine not found")))
  def getStateMachine(): Route = get {
    path("state-machine" / LongNumber) { id =>
      val r = api.getStateMachine(Id(id))
      r match {
        case Some(x) => complete(x)
        case None    => complete(StatusCodes.NotFound)
      }
    }
  }

  @Path("{id}")
  @ApiOperation(httpMethod = "DELETE", response = classOf[StatusCode], value = "Returns the status code")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Successful deletion"),
      new ApiResponse(code = 404, message = "State machine not found")
    ))
  def deleteStateMachine(): Route = delete {
    path("state-machine" / LongNumber) { stateMachineId =>
      import de.retest.guistatemachine.api.Id
      val r = api.removeStateMachine(Id(stateMachineId))
      complete(if (r) StatusCodes.OK else StatusCodes.NotFound)
    }
  }

  @ApiOperation(httpMethod = "POST", response = classOf[Id], value = "Returns the ID")
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Successful creation")))
  def postStateMachine(): Route = post {
    path("state-machine") {
      val id = api.createStateMachine()
      complete(id)
    }
  }

  @Path("{id}/get-state")
  @ApiOperation(httpMethod = "POST", response = classOf[State], value = "Returns the existing or newly created state")
  @ApiImplicitParam(name = "state-body",
                    value = "The descriptors and the initial never explored actions of the state",
                    required = true,
                    dataType = "GetStateBody",
                    paramType = "body")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "State machine not found")))
  def getState(): Route = post {
    path("state-machine" / LongNumber / "get-state") { id =>
      val app = api.getStateMachine(Id(id))
      app match {
        case Some(x) =>
          entity(as[GetStateBody]) { body =>
            complete(x.getState(body.descriptors, body.neverExploredActions))
          }
        case None => complete(StatusCodes.NotFound)
      }
    }
  }

  @Path("{id}/execute-state")
  @ApiOperation(httpMethod = "POST", response = classOf[State], value = "Returns the state which is reached by executing this action")
  @ApiImplicitParam(
    name = "execute-action-body",
    value = "The source state, the action and the target state in form of its descriptors and initial never explored actions",
    required = true,
    dataType = "ExecuteActionBody",
    paramType = "body"
  )
  @ApiResponses(Array(new ApiResponse(code = 404, message = "State machine not found")))
  def executeAction(): Route = post {
    path("state-machine" / LongNumber / "execute-action") { id =>
      val app = api.getStateMachine(Id(id))
      app match {
        case Some(x) =>
          entity(as[ExecuteActionBody]) { body =>
            complete(x.executeAction(body.from, body.a, body.descriptors, body.neverExploredActions))
          }
        case None => complete(StatusCodes.NotFound)
      }
    }
  }
}
