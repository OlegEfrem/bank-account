package com.oef.bank.account.domain.service.implementations

import com.oef.bank.UnitSpec
import com.oef.bank.account.domain.model.{Account, AccountId, AccountNotFoundException}
import com.oef.bank.account.domain.service.provided.DataStore
import com.oef.bank.account.infrastructure.outbound.store.memory.InMemoryStore
import org.joda.money.{CurrencyUnit, Money}
import org.scalatest.OneInstancePerTest

class SimpleAccountServiceTest extends UnitSpec with OneInstancePerTest {

  "SimpleAccountService" - {
    val accountId                 = AccountId(123, 123456, CurrencyUnit.GBP)
    def money(amount: BigDecimal) = Money.of(CurrencyUnit.GBP, amount.underlying())
    val money_22                  = money(22.35)
    val account_0                 = Account(accountId)
    val account_22                = account_0.copy(balance = money_22)
    val store: DataStore          = new InMemoryStore
    val service                   = new SimpleAccountService(store)

    "deposit should" - {
      store.create(accountId)

      "add money to an existing account" in {
        service.deposit(money_22, accountId).futureValue shouldBe account_22
        store.read(accountId).futureValue shouldBe account_22
      }

      "return error for non existing account" in {
        whenReady(service.deposit(money_22, accountId.copy(sortCode = -1, currencyUnit = CurrencyUnit.GBP)).failed) { e =>
          e shouldBe an[AccountNotFoundException]
        }
      }

      "forbid negative amount operation" in {
        whenReady(service.deposit(money(-10), accountId).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

    }

    "withdraw should" - {
      store.create(accountId)

      "withdraw money from an existing account" in {
        val account_44 = account_22 + money_22
        store.add(account_44.balance.toTransaction, account_44.id)
        store.read(accountId).futureValue shouldBe account_44
        service.withdraw(money_22, accountId).futureValue shouldBe account_22
        store.read(accountId).futureValue shouldBe account_22
      }

      "return error for non existing account" in {
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
        whenReady(service.withdraw(money(100), accountId).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }
    }

    "transfer should" - {
      "withdraw from source account and deposit to destination account" in {
        val sourceAccount = Account(AccountId(1, 12, CurrencyUnit.GBP), money(100))
        store.create(sourceAccount.id)
        store.add(sourceAccount.balance.toTransaction, sourceAccount.id)
        val destinationAccount = account_0
        store.create(destinationAccount.id)
        val transferAmount           = money(50)
        val sourceAfterTransfer      = sourceAccount - transferAmount
        val destinationAfterTransfer = destinationAccount + transferAmount
        service.transfer(transferAmount, sourceAccount.id, destinationAccount.id).futureValue shouldBe (sourceAfterTransfer, destinationAfterTransfer)
        store.read(sourceAccount.id).futureValue shouldBe sourceAfterTransfer
        store.read(destinationAccount.id).futureValue shouldBe destinationAfterTransfer
      }
    }

  }

}
