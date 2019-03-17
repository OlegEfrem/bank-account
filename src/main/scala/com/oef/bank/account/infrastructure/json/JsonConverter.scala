package com.oef.bank.account.infrastructure.json
import com.oef.bank.account.infrastructure.json.jackson.JsonConverterJackson

trait JsonConverter {
  def fromJson[ToType](json: String)(implicit m: Manifest[ToType]): ToType
  def toJson(obj: Any): String
}

object JsonConverter {
  def apply(): JsonConverter = JsonConverterJackson
}
