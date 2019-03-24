package com.oef.bank.account.domain.model
import org.joda.money.{CurrencyUnit, Money}

case class AccountId(sortCode: Int, accNumber: Long, currencyUnit: CurrencyUnit) {
  override def toString: String = s"AccountId(sortCode: $sortCode, accNumber: $accNumber, currencyUnit: ${currencyUnit.getCode})"
}

case class Account(id: AccountId, balance: Money) {

  def +(money: Money): Account = this.copy(balance = balance plus money)

  def -(money: Money): Account = this.copy(balance = balance minus money)

}

object Account {

  def apply(id: AccountId, currency: String = "GBP", amount: BigDecimal = 0): Account = {
    val currencyUnit = CurrencyUnit.of(currency)
    val money        = Money.of(currencyUnit, amount.underlying())
    new Account(id, money)
  }
}
