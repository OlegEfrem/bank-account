package com.oef.bank

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers, OptionValues}
import scala.concurrent.duration._

trait BaseSpec extends FreeSpec with Matchers with ScalaFutures with OptionValues {
  implicit val patience: PatienceConfig = PatienceConfig(5 seconds, 100 millis)
}
