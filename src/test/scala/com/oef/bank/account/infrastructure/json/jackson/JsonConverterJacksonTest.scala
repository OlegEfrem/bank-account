package com.oef.bank.account.infrastructure.json.jackson

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.oef.bank.UnitSpec
import com.oef.bank.account.domain.model.AccountId
import com.oef.bank.account.infrastructure.inbound.http.{ApiMoney, ApiTransfer}

class JsonConverterJacksonTest extends UnitSpec {
  val jsonConverter: JsonConverterJackson.type = JsonConverterJackson

  "Converter should" - {
    val transfer = ApiTransfer(AccountId(1, 2), AccountId(2, 3), ApiMoney("GBP", 20))
    val json     = """{"from":{"sortCode":1,"accNumber":2},"to":{"sortCode":2,"accNumber":3},"money":{"currency":"GBP","amount":20}}"""

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
