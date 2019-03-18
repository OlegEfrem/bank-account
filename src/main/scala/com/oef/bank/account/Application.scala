package com.oef.bank.account

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import com.oef.bank.account.domain.service.AccountService
import com.oef.bank.account.domain.service.provided.DataStore
import com.oef.bank.account.infrastructure.config.{ActorContext, AppConfig}
import com.oef.bank.account.infrastructure.inbound.http.{AccountRoutes, RestApi}
import com.oef.bank.account.infrastructure.json.JsonConverter
import com.oef.bank.account.infrastructure.outbound.store.memory.InMemoryStore

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object Application extends App with AppConfig with ActorContext {
  val log: LoggingAdapter                       = Logging(system, getClass)
  val store: DataStore                          = new InMemoryStore()
  val converter: JsonConverter                  = JsonConverter()
  val service: AccountService                   = AccountService(store)
  val routes: AccountRoutes                     = new AccountRoutes(service, converter)
  val restApi                                   = RestApi(routes)
  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(restApi.routes, httpInterface, httpPort)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
  //#http-server
  //#main-class
}
