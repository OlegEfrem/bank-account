package com.oef.bank.account.domain.service
import com.oef.bank.account.domain.model.Transaction
import org.joda.money.Money

trait Implicits {
  implicit class RichMoney(money: Money) {
    def toTransaction: Transaction = Transaction(money.getAmount)
  }
}
