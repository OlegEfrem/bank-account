package com.oef.bank.account.infrastructure.inbound.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class RestApi(accountRoutes: AccountRoutes) extends RejectionHandling {
  val routes: Route =
    handleRejections(customRejectionHandler) {
      concat(
        pathPrefix("info") {
          complete("A simple Bank Account Http api, for details see: https://github.com/OlegEfrem/bank-account")
        },
        pathPrefix("v1") {
          accountRoutes.endpoints
        }
      )
    }

}

object RestApi {
  def apply(accountRoutes: AccountRoutes): RestApi = new RestApi(accountRoutes)
}
