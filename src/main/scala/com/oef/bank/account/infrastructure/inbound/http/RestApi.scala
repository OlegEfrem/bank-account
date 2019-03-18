package com.oef.bank.account.infrastructure.inbound.http

import akka.http.scaladsl.server.Directives.{handleRejections, pathPrefix}
import akka.http.scaladsl.server.Route

class RestApi(accountRoutes: AccountRoutes) extends RejectionHandling {
  val routes: Route =
    handleRejections(customRejectionHandler) {
      pathPrefix("v1") {
        accountRoutes.endpoints
      }
    }

}

object RestApi {
  def apply(accountRoutes: AccountRoutes): RestApi = new RestApi(accountRoutes)
}
