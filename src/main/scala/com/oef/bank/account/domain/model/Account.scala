package com.oef.bank.account.domain.model
import org.joda.money.Money

case class AccountId(sortCode: Int, accNumber: Long)

case class Account(number: AccountId, balance: Money) {
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
