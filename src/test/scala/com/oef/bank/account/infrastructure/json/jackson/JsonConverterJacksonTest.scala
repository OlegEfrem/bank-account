package com.oef.bank.account.infrastructure.json.jackson

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.oef.bank.UnitSpec
import com.oef.bank.account.infrastructure.inbound.http.{ApiAccountId, ApiMoney, ApiTransfer}

class JsonConverterJacksonTest extends UnitSpec {
  val jsonConverter: JsonConverterJackson.type = JsonConverterJackson

  "Converter should" - {
    val transfer = ApiTransfer(ApiAccountId(1, 2, "GBP"), ApiAccountId(2, 3, "GBP"), ApiMoney("GBP", 20))
    val json     =
      //scalastyle:off
      """{"from":{"sortCode":1,"accNumber":2,"currency":"GBP"},"to":{"sortCode":2,"accNumber":3,"currency":"GBP"},"money":{"currency":"GBP","amount":20}}"""
    //scalastyle:on

    "convert from json to case class" in {
      jsonConverter.fromJson[ApiTransfer](json) shouldBe transfer

    }

    "convert from case class to json" in {
      jsonConverter.toJson(transfer) shouldBe json
    }

    "throw an exception when trying to convert a ConversionRequest json to a ConversionResponse object" in {
      an[UnrecognizedPropertyException] should be thrownBy jsonConverter.fromJson[ApiMoney](json)
    }
  }

}
