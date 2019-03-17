package com.oef.bank.account.infrastructure.inbound.http

import akka.http.scaladsl.server.Directives.{handleRejections, pathPrefix}
import akka.http.scaladsl.server.Route

class RestApi(transferRoutes: AccountRoutes) extends RejectionHandling {
  val routes: Route =
    handleRejections(customRejectionHandler) {
      pathPrefix("v1") {
        transferRoutes.endpoints
      }
    }

}

object RestApi {
  def apply(): RestApi = new RestApi(AccountRoutes())
}
