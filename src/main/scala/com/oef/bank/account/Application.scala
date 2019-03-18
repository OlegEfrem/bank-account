package com.oef.bank.account

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import com.oef.bank.account.domain.service.AccountService
import com.oef.bank.account.domain.service.provided.DataStore
import com.oef.bank.account.infrastructure.config.{ActorContext, AppConfig}
import com.oef.bank.account.infrastructure.inbound.http.{AccountRoutes, RestApi}
import com.oef.bank.account.infrastructure.json.JsonConverter
import com.oef.bank.account.infrastructure.outbound.store.memory.InMemoryStore

object Application extends App with AppConfig with ActorContext {
  val log: LoggingAdapter      = Logging(system, getClass)
  val store: DataStore         = new InMemoryStore()
  val converter: JsonConverter = JsonConverter()
  val service: AccountService  = AccountService(store)
  val routes: AccountRoutes    = new AccountRoutes(service, converter)
  val restApi                  = RestApi(routes)
  Http().bindAndHandle(restApi.routes, httpInterface, httpPort)
}
