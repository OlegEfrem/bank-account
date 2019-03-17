package com.oef.bank.account.domain.service.implementations

import com.oef.bank.UnitSpec
import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.provided.DataStore
import org.joda.money.{CurrencyUnit, Money}
import scala.concurrent.Future

class SimpleAccountServiceTest extends UnitSpec {

  "SimpleAccountService" - {
    val storeStub                 = stub[DataStore]
    val accountId                 = AccountId(123, 123456)
    def money(amount: BigDecimal) = Money.of(CurrencyUnit.GBP, amount.underlying())
    val money_22                  = money(22.35)
    val account_0                 = Account(accountId)
    val account_22                = account_0.copy(balance = money_22)
    val service                   = new SimpleAccountService(storeStub)

    "deposit should" - {

      "add money to an existing account" in {
        storeStub.read _ when * returns Future.successful(account_0)
        storeStub.update _ when account_22 returns Future.successful(account_0)
        service.deposit(money_22, accountId).futureValue
        storeStub.update _ verify account_22
      }

      "return error for non existing account" in {
        storeStub.read _ when accountId returns Future.failed(new IllegalArgumentException("account not found."))
        whenReady(service.deposit(money_22, accountId).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

      "forbid negative amount operation" in {
        storeStub.read _ when * returns Future.successful(account_0)
        whenReady(service.deposit(money(-10), accountId).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

    }

    "withdraw should" - {

      "withdraw money from an existing account" in {
        storeStub.read _ when * returns Future.successful(account_22 + money_22)
        storeStub.update _ when account_22 returns Future.successful(account_22)
        service.withdraw(money_22, accountId).futureValue
        storeStub.update _ verify account_22
      }

      "return error for non existing account" in {
        storeStub.read _ when accountId returns Future.failed(new IllegalArgumentException("account not found."))
        whenReady(service.withdraw(money_22, accountId).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

      "forbid negative amount operation" in {
        whenReady(service.withdraw(money(-10), accountId).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

      "forbid overdraft" in {
        storeStub.read _ when * returns Future.successful(Account(accountId, money(50)))
        whenReady(service.withdraw(money(100), accountId).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }
    }

    "transfer should" - {}

  }

}
