package com.oef.bank.account.infrastructure.config

import com.typesafe.config.ConfigFactory

trait AppConfig {
  private val config        = ConfigFactory.load()
  private val httpConfig    = config.getConfig("http")
  val httpInterface: String = httpConfig.getString("interface")
  val httpPort: Int         = httpConfig.getInt("port")
}
