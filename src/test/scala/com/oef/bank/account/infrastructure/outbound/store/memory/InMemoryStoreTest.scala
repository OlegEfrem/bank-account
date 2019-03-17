package com.oef.bank.account.infrastructure.outbound.store.memory

import com.oef.bank.UnitSpec
import com.oef.bank.account.domain.model.{Account, AccountId}
import org.joda.money.{CurrencyUnit, Money}

class InMemoryStoreTest extends UnitSpec {

  "InMemoryStore" - {
    def store     = new InMemoryStore
    val accountId = AccountId(1234, 123456)
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
          e shouldBe an[IllegalArgumentException]

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
          e shouldBe an[IllegalArgumentException]
        }
      }
    }

    "update should" - {
      "update an existing account returning the account state before the update" in {
        val store = new InMemoryStore
        store.create(accountId).futureValue.balance shouldBe gbp_0
        val gbp_20 = Money.of(CurrencyUnit.GBP, 20)
        store.update(account.copy(balance = gbp_20)).futureValue.balance shouldBe gbp_0
        store.read(accountId).futureValue.balance shouldBe gbp_20

      }

      "return an error on non existing account" in {
        whenReady(store.update(account).failed) { e =>
          e shouldBe an[IllegalArgumentException]
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
          e shouldBe an[IllegalArgumentException]
        }
      }
    }

  }

}
