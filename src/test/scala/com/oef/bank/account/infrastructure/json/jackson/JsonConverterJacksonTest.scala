package com.oef.bank.account.infrastructure.json.jackson

import java.io.InputStream
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.oef.bank.UnitSpec
import com.oef.bank.account.infrastructure.inbound.http.{ConversionRequest, ConversionResponse}

class JsonConverterJacksonTest extends UnitSpec {
  val jsonConverter: JsonConverterJackson.type = JsonConverterJackson
  import JsonConverterJacksonTest._

  "fromJson should" - {

    "convert a ConversionRequest json to a ConversionRequest object " in {
      jsonConverter.fromJson[ConversionRequest](conversionRequestJson) shouldBe conversionRequest
    }

    "convert a ConversionResponse json to ConversionResponse object" in {
      jsonConverter.fromJson[ConversionResponse](conversionResponseJson) shouldBe conversionResponse
    }

    "throw an exception when trying to convert a ConversionRequest json to a ConversionResponse object" in {
      an[UnrecognizedPropertyException] should be thrownBy jsonConverter.fromJson[ConversionResponse](conversionRequestJson)
    }
  }

  "toJson should" - {
    "convert a ConversionRequest to json" in {
      jsonConverter.toJson(conversionRequest) shouldBe conversionRequestJson
    }

    "convert a ConversionResponse to json" in {
      jsonConverter.toJson(conversionResponse) shouldBe conversionResponseJson
    }
  }

}

object JsonConverterJacksonTest {
  val emptyJson: InputStream   = readFile("/json/Empty.json")
  val emptyFile: InputStream   = readFile("/json/EmptyFile")
  val invalidJson: InputStream = readFile("/json/InvalidJson")
  val json1Level: InputStream  = readFile("/json/1Level.json")
  val json3Levels: InputStream = readFile("/json/3Levels.json")
  val json3LevelsMap: Map[String, Any] = Map(
    "id"   -> 10001,
    "name" -> "Alex Smith",
    "payments" ->
      List(Map("timestamp" -> "ISO_DATE", "amount" -> 100.59), Map("timestamp" -> "ISO_DATE", "amount" -> 12.99)),
    "address" ->
      Map("postcode" -> "W1 5AX",
          "coordiantes" ->
            Map("lat" -> 51, "lon" -> 0),
          "country" -> "UK",
          "second"  -> "Kings Cross",
          "first"   -> "12 Watergarden")
  )
  val conversionRequest: ConversionRequest = ConversionRequest("GBP", "EUR", 102.6)
  val conversionRequestJson: String =
    """
      |{
      |"fromCurrency": "GBP",
      |"toCurrency" : "EUR",
      |"amount" : 102.6
      |}
    """.stripMargin.replaceAll("""\n|\p{Blank}""", "")
  val conversionResponse: ConversionResponse = ConversionResponse(1.11, 113.886, 102.6)
  val conversionResponseJson: String =
    """
      |{
      |"exchange" : 1.11,
      |"amount" : 113.886,
      |"original" : 102.6
      |}
    """.stripMargin.replaceAll("""\n|\p{Blank}""", "")

  private def readFile(fileName: String): InputStream = getClass.getResourceAsStream(fileName)
}
