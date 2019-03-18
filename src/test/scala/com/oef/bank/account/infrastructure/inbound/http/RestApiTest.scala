package com.oef.bank.account.infrastructure.inbound.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCode}
import com.oef.bank.ApiSpec
import com.oef.bank.account.domain.model.{Account, AccountId}
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
    val account = Account(AccountId(1, 2))

    "create a new account responding with HTTP-201" in {
      Put(generalUrl(account.id)) ~> restApi.routes ~> check {
        status shouldBe Created
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2},"balance":{"currency":"GBP","amount":0.00}}"""
      }
    }

    "return an existing account" in {
      store.create(account.id)
      Get(generalUrl(account.id)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2},"balance":{"currency":"GBP","amount":0.00}}"""
      }
    }

    "delete an existing account" in {
      store.create(account.id)
      Delete(generalUrl(account.id)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2},"balance":{"currency":"GBP","amount":0.00}}"""
      }
    }

    "deposit money to an existing account" in {
      store.create(account.id)
      val money = ApiMoney("GBP", 20)
      Post(generalUrl(account.id) + "deposit", requestEntity(money)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2},"balance":{"currency":"GBP","amount":20.00}}"""
      }
    }

    "withdraw money from an existing account" in {
      store.create(account.id)
      store.update(Account(account.id, amount = 50))
      val money = ApiMoney("GBP", 20)
      Post(generalUrl(account.id) + "withdraw", requestEntity(money)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String] shouldBe """{"id":{"sortCode":1,"accNumber":2},"balance":{"currency":"GBP","amount":30.00}}"""
      }
    }

    "transfer money from an existing account to another existing account" in {
      store.create(account.id)
      store.update(Account(account.id, amount = 50))
      val anotherAccountId = AccountId(-1, -22)
      val money            = ApiMoney("GBP", 20)
      val transfer         = Transfer(anotherAccountId, money)
      store.create(anotherAccountId)
      Post(generalUrl(account.id) + "transfer", requestEntity(transfer)) ~> restApi.routes ~> check {
        status shouldBe OK
        //scalastyle:off
        responseAs[String] shouldBe
          """{"fromAccount":{"id":{"sortCode":1,"accNumber":2},"balance":{"currency":"GBP","amount":30.00}},"toAccount":{"id":{"sortCode":-1,"accNumber":-22},"balance":{"currency":"GBP","amount":20.00}}}""".stripMargin
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
      Patch(generalUrl(account.id)) ~> restApi.routes ~> check {
        status shouldBe MethodNotAllowed
        responseAs[String] shouldBe "Not supported method! Supported methods are: GET, PUT, DELETE!"
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
      verifyExceptionMappedToCode(new Exception("some error"), InternalServerError)
    }

    def verifyExceptionMappedToCode(exception: Exception, code: StatusCode): Unit = {
      val request = ConversionRequest("Eur", "GBP", 2)
      val entity  = HttpEntity(MediaTypes.`application/json`, jsonConverter.toJson(request))
    }

  }

  def requestEntity(obj: AnyRef) = HttpEntity(MediaTypes.`application/json`, jsonConverter.toJson(obj))

  def generalUrl(id: AccountId): String = {
    import id._
    s"/v1/account/sort-code/$sortCode/acc-no/$accNumber/"
  }

}
