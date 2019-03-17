package com.oef.bank.account

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import com.oef.bank.account.infrastructure.config.{ActorContext, AppConfig}
import com.oef.bank.account.infrastructure.inbound.http.RestApi

object Application extends App with AppConfig with ActorContext {
  val log: LoggingAdapter = Logging(system, getClass)
  val restApi             = RestApi()
  Http().bindAndHandle(restApi.routes, httpInterface, httpPort)
}
