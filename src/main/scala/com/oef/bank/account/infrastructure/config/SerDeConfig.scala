package com.oef.bank.account.infrastructure.config

import java.text.{DateFormat, SimpleDateFormat}

object SerDeConfig {
  val datePattern: String    = "yyyy-MM-dd"
  val dateFormat: DateFormat = new SimpleDateFormat(datePattern)
}
