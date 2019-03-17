package com.oef.bank.account.infrastructure.inbound.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.oef.bank.account.infrastructure.json.JsonConverter

class AccountRoutes(jsonConverter: JsonConverter) {

  val endpoints: Route = pathPrefix("account") {
    pathEndOrSingleSlash {
      get {
        complete("This is a response to GET from the server")
      } ~ put {
        complete("This is a response to PUT from the server")
      } ~ post {
        complete("This is a response to POST from the server")
      }
    }
  }

}

object AccountRoutes {
  def apply(): AccountRoutes = new AccountRoutes(JsonConverter())
}
