package com.oef.bank.account.infrastructure.json.jackson

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.oef.bank.account.infrastructure.config.SerDeConfig
import com.oef.bank.account.infrastructure.json.JsonConverter

object JsonConverterJackson extends JsonConverter {
  private val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.setDateFormat(SerDeConfig.dateFormat)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

  override def fromJson[ToType](json: String)(implicit m: Manifest[ToType]): ToType = {
    mapper.readValue[ToType](json)
  }

  override def toJson(obj: Any): String = {
    mapper.writeValueAsString(obj)
  }
}
