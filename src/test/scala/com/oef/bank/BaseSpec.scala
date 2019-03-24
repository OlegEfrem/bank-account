package com.oef.bank

import com.oef.bank.account.domain.service.Implicits
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers, OptionValues}

import scala.concurrent.duration._

trait BaseSpec extends FreeSpec with Matchers with ScalaFutures with OptionValues with Implicits {
  implicit val patience: PatienceConfig = PatienceConfig(5 seconds, 100 millis)
}
