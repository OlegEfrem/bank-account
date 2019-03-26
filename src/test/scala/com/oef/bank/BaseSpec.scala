package com.oef.bank

import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.Implicits
import org.joda.money.{CurrencyUnit, Money}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers, OptionValues}
import scala.concurrent.duration._
import scala.util.Random

trait BaseSpec extends FreeSpec with Matchers with ScalaFutures with OptionValues with Implicits {
  implicit val patience: PatienceConfig = PatienceConfig(5 seconds, 100 millis)

  def randomSortCode(): Int = Random.nextInt(1000000)

  def randomAccNumber(): Long = Random.nextLong()

  def randomAccount(): Account = Account(AccountId(randomSortCode(), randomAccNumber(), CurrencyUnit.GBP))

  def money(amount: BigDecimal): Money = Money.of(CurrencyUnit.GBP, amount.underlying())

  def accountWith(amount: BigDecimal): Account =
    randomAccount().copy(balance = money(amount))
}
