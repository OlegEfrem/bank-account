package com.oef.bank.account.domain.model
import org.joda.money.{CurrencyUnit, Money}

case class AccountId(sortCode: Int, accNumber: Long)

case class Account(id: AccountId, balance: Money) {
  def deposit(money: Money): Account = {
    requirePositive(money)
    this.copy(balance = balance plus money)
  }
  def withdraw(money: Money): Account = {
    requirePositive(money)
    require(balance.minus(money).isPositiveOrZero, s"insufficient funds: available $balance, required $money ")
    this.copy(balance = balance minus money)
  }

  private def requirePositive(money: Money): Unit = require(money.isPositive, s"negative amount forbidden: $money")
}

object Account {
  def apply(id: AccountId, currency: String = "GBP", amount: BigDecimal = 0): Account = {
    val currencyUnit = CurrencyUnit.of(currency)
    val money        = Money.of(currencyUnit, amount.underlying())
    new Account(id, money)
  }
}
