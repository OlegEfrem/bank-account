package com.oef.bank.account.infrastructure.inbound.http
import com.oef.bank.account.domain.model.{Account, AccountId}
import org.joda.money.{CurrencyUnit, Money}

case class ApiMoney(currency: String, amount: BigDecimal) {
  def toDomain: Money = Money.of(CurrencyUnit.of(currency), amount.underlying())
}

case class ApiAccountId(sortCode: Int, accNumber: Long, currencyUnit: String) {
  def toDomain: AccountId = AccountId(sortCode, accNumber, CurrencyUnit.of(currencyUnit))
}

object ApiAccountId {
  def apply(accountId: AccountId): ApiAccountId = new ApiAccountId(accountId.sortCode, accountId.accNumber, accountId.currencyUnit.getCode)
}

case class ApiAccount(id: ApiAccountId, balance: ApiMoney) {
  def toDomain: Account = Account(id.toDomain, balance.currency, balance.amount)
}

object ApiAccount {
  def apply(account: Account): ApiAccount =
    ApiAccount(ApiAccountId(account.id), ApiMoney(account.balance.getCurrencyUnit.getCode, account.balance.getAmount))
  def apply(id: ApiAccountId, amount: BigDecimal): ApiAccount = ApiAccount(id, ApiMoney(id.currencyUnit, amount))
}

case class ApiDeposit(to: ApiAccountId, money: ApiMoney)

case class ApiWithdraw(from: ApiAccountId, money: ApiMoney)

case class ApiTransfer(from: ApiAccountId, to: ApiAccountId, money: ApiMoney)
