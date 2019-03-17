package com.oef.bank.account.domain.service.implementations
import com.oef.bank.UnitSpec
import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.provided.DataStore
import org.joda.money.{CurrencyUnit, Money}
import scala.concurrent.Future

class SimpleAccountServiceTest extends UnitSpec {

  "SimpleAccountService" - {
    val storeStub  = stub[DataStore]
    val accountId  = AccountId(123, 123456)
    val money      = Money.of(CurrencyUnit.GBP, 22.35)
    val account_0  = Account(accountId)
    val account_22 = account_0.copy(balance = money)

    def service = new SimpleAccountService(storeStub)

    "deposit should" - {
      "add money to an existing account" in {
        storeStub.read _ when accountId returns Future.successful(account_0)
        storeStub.update _ when account_22 returns Future.successful(account_0)
        service.deposit(money, accountId).futureValue
        storeStub.update _ verify account_22
      }

      "return error for non existing account" in {
        storeStub.read _ when accountId returns Future.failed(new IllegalArgumentException("account not found."))
        whenReady(service.deposit(money, accountId).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }
    }

    "withdraw should" - {}

    "transfer should" - {}

  }

}
