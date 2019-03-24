package com.oef.bank.account.infrastructure.inbound.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import com.oef.bank.ApiSpec
import com.oef.bank.account.domain.model.Transaction
import com.oef.bank.account.domain.service.AccountService
import com.oef.bank.account.domain.service.provided.DataStore
import com.oef.bank.account.infrastructure.json.JsonConverter
import com.oef.bank.account.infrastructure.outbound.store.memory.InMemoryStore
import org.scalatest.OneInstancePerTest

class RestApiTest extends ApiSpec with OneInstancePerTest {
  val store: DataStore        = new InMemoryStore()
  val jsonConverter           = JsonConverter()
  val service: AccountService = AccountService(store)
  val restApi                 = new RestApi(new AccountRoutes(service, JsonConverter()))

  "restApi should" - {

    "create a new account responding with HTTP-201" in {
      Put("/v1/account", requestEntity(apiAccount.id)) ~> restApi.routes ~> check {
        status shouldBe Created
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2,"currency":"GBP"},"balance":{"currency":"GBP","amount":0.00}}"""
      }
    }

    "return an existing account" in {
      store.create(apiAccount.id.toDomain)
      Get("/v1/account?sort-code=1&acc-no=2&currency=GBP") ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2,"currency":"GBP"},"balance":{"currency":"GBP","amount":0.00}}"""
      }
    }

    "delete an existing account" in {
      store.create(apiAccount.id.toDomain)
      Delete("/v1/account", requestEntity(apiAccount.id)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2,"currency":"GBP"},"balance":{"currency":"GBP","amount":0.00}}"""
      }
    }

    "deposit money to an existing account" in {
      store.create(apiAccount.id.toDomain)
      Post("/v1/account/deposit", requestEntity(apiDeposit)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2,"currency":"GBP"},"balance":{"currency":"GBP","amount":20.00}}"""
      }
    }

    "withdraw money from an existing account" in {
      store.create(apiAccount.id.toDomain)
      store.add(Transaction(50), apiAccount.id.toDomain)
      Post("/v1/account/withdrawal", requestEntity(apiWithdraw)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2,"currency":"GBP"},"balance":{"currency":"GBP","amount":30.00}}"""
      }
    }

    "transfer money from an existing account to another existing account" in {
      store.create(apiAccount.id.toDomain)
      store.add(Transaction(50), apiAccount.id.toDomain)
      val anotherAccountId = ApiAccountId(-1, -22, "GBP")
      val transfer         = ApiTransfer(apiAccount.id, anotherAccountId, apiMoney)
      store.create(anotherAccountId.toDomain)
      Post("/v1/account/transfer", requestEntity(transfer)) ~> restApi.routes ~> check {
        status shouldBe OK
        //scalastyle:off
        responseAs[String] shouldBe
          """{"fromAccount":{"id":{"sortCode":1,"accNumber":2,"currency":"GBP"},"balance":{"currency":"GBP","amount":30.00}},"toAccount":{"id":{"sortCode":-1,"accNumber":-22,"currency":"GBP"},"balance":{"currency":"GBP","amount":20.00}}}""".stripMargin
        //scalastyle:on
      }
    }

    s"respond with HTTP-$NotFound for a non existing path" in {
      Post("/non/existing/") ~> restApi.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "The path you requested [/non/existing/] does not exist."
      }
    }

    s"respond with HTTP-$MethodNotAllowed for a non supported HTTP method" in {
      Patch("/v1/account/") ~> restApi.routes ~> check {
        status shouldBe MethodNotAllowed
        responseAs[String] shouldBe "Not supported method! Supported methods are: PUT, DELETE!"
      }
    }

    s"respond with HTTP-$BadRequest in case of an " in {
      pending
    }

    s"respond with HTTP-$BadGateway in case of a " in {
      pending
    }

    s"respond with HTTP-$NotFound in case of a " in {
      pending
    }

    s"respond with HTTP-$InternalServerError in case of a generic ${classOf[Exception].getSimpleName}" in {
      pending
    }

  }

  val apiMoney    = ApiMoney("GBP", 20)
  val apiAccount  = ApiAccount(ApiAccountId(1, 2, "GBP"), ApiMoney("GBP", 0))
  val apiDeposit  = ApiDeposit(apiAccount.id, apiMoney)
  val apiWithdraw = ApiWithdraw(apiAccount.id, apiMoney)

  def requestEntity(obj: AnyRef): HttpEntity.Strict = {
    val json = jsonConverter.toJson(obj)
    HttpEntity(MediaTypes.`application/json`, json)
  }

}
