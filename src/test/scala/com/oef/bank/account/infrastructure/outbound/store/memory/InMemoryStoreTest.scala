package com.oef.bank.account.infrastructure.outbound.store.memory

import com.oef.bank.UnitSpec
import com.oef.bank.account.domain.model._
import org.joda.money.{CurrencyUnit, Money}

class InMemoryStoreTest extends UnitSpec {

  "InMemoryStore" - {
    def store     = new InMemoryStore
    val accountId = AccountId(1234, 123456, CurrencyUnit.GBP)
    val gbp_0     = Money.of(CurrencyUnit.GBP, 0)
    val account   = Account(accountId, gbp_0)

    "create should" - {
      "insert a new account with zero money" in {
        store.create(accountId).futureValue shouldBe account
      }

      "return error on attempt to re-create an existing account" in {
        val store = new InMemoryStore
        store.create(accountId).futureValue shouldBe account
        whenReady(store.create(accountId).failed) { e =>
          e shouldBe an[AccountAlreadyExistsException]

        }
      }
    }

    "read should" - {
      "return an existing account" in {
        val store = new InMemoryStore
        store.create(accountId).futureValue shouldBe account
        store.read(accountId).futureValue shouldBe account
      }

      "return error on non existing account" in {
        whenReady(store.read(accountId).failed) { e =>
          e shouldBe an[AccountNotFoundException]
        }
      }
    }

    "add should" - {
      def verifyAdd(amount: Int) = {
        val store = new InMemoryStore
        store.create(accountId).futureValue shouldBe Account(accountId)
        store.add(Transaction(amount), accountId).futureValue shouldBe Money.of(accountId.currencyUnit, amount)
        store.read(accountId).futureValue shouldBe Account(accountId, accountId.currencyUnit.getCode, amount)
      }
      "add a positive amount to transactions" in {
        verifyAdd(10)
      }

      "add a negative amount to transactions" in {
        verifyAdd(-10)
      }

      "return error on non existing account" in {
        whenReady(store.add(Transaction(10), accountId).failed) { e =>
          e shouldBe an[AccountNotFoundException]
        }
      }
    }

    "delete should" - {
      "remove an existing account returning deleted account" in {
        val store = new InMemoryStore
        store.create(accountId).futureValue shouldBe account
        store.delete(accountId).futureValue shouldBe account
      }

      "return error on non existing account" in {
        whenReady(store.delete(accountId).failed) { e =>
          e shouldBe an[AccountNotFoundException]
        }
      }
    }

  }

}
