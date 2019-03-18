package com.oef.bank.account.infrastructure.inbound.http
import com.oef.bank.account.domain.model.{Account, AccountId}
import org.joda.money.{CurrencyUnit, Money}

case class ApiMoney(currency: String, amount: BigDecimal) {
  def toDomain: Money = Money.of(CurrencyUnit.of(currency), amount.underlying())
}

case class ApiAccount(id: AccountId, balance: ApiMoney) {
  def toDomain: Account = Account(id, balance.currency, balance.amount)
}

object ApiAccount {
  def apply(account: Account): ApiAccount = ApiAccount(account.id, ApiMoney(account.balance.getCurrencyUnit.getCode, account.balance.getAmount))
}

case class Transfer(to: AccountId, money: ApiMoney)
