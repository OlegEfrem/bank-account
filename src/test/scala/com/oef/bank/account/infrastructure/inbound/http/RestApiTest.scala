package com.oef.bank.account.infrastructure.inbound.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCode}
import com.oef.bank.ApiSpec
import com.oef.bank.account.infrastructure.json.JsonConverter

class RestApiTest extends ApiSpec {

  "restApi should" - {

    s"respond with HTTP-$OK when submitting valid currency conversions" in {

    }

    s"respond with HTTP-$NotFound for a non existing path" in {
      Post("/non/existing/") ~> restApi.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "The path you requested [/non/existing/] does not exist."
      }
    }

    s"respond with HTTP-$MethodNotAllowed for a non supported HTTP method" in {
      Patch(generalUrl) ~> restApi.routes ~> check {
        status shouldBe MethodNotAllowed
        responseAs[String] shouldBe "Not supported method! Supported methods are: GET, PUT, POST!"
      }
    }

    s"respond with HTTP-$BadRequest in case of an " in {
    }

    s"respond with HTTP-$BadGateway in case of a " in {
    }

    s"respond with HTTP-$NotFound in case of a " in {
    }

    s"respond with HTTP-$InternalServerError in case of a generic ${classOf[Exception].getSimpleName}" in {
      verifyExeptionMappedToCode(new Exception("some error"), InternalServerError)
    }

    def verifyExeptionMappedToCode(exception: Exception, code: StatusCode): Unit = {
      val request = ConversionRequest("Eur", "GBP", 2)
      val entity  = HttpEntity(MediaTypes.`application/json`, jsonConverter.toJson(request))
    }

  }

  val conversionRequest: ConversionRequest   = ConversionRequest("GBP", "EUR", 100)
  val conversionRequestJson                  = """{"fromCurrency": "GBP","toCurrency":"EUR","amount":100}"""
  val conversionResponse: ConversionResponse = ConversionResponse(1.2, 120, 100)
  val conversionResponseJson                 = """{"exchange":1.2,"amount":120,"original":100}"""
  val jsonConverter                          = JsonConverter()
  val requestEntity                          = HttpEntity(MediaTypes.`application/json`, jsonConverter.toJson(conversionRequest))
  val restApi                                = new RestApi(new AccountRoutes(JsonConverter()))
  val generalUrl                             = "/v1/account/"

}
