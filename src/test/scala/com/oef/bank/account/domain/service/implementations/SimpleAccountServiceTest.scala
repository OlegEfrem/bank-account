package com.oef.bank.account.domain.service.implementations

import java.util.concurrent.Executors
import com.oef.bank.UnitSpec
import com.oef.bank.account.domain.model.{Account, AccountId, AccountNotFoundException}
import com.oef.bank.account.domain.service.provided.DataStore
import com.oef.bank.account.infrastructure.outbound.store.memory.InMemoryStore
import org.joda.money.CurrencyUnit
import org.scalatest.OneInstancePerTest
import scala.concurrent.{ExecutionContext, Future}

class SimpleAccountServiceTest extends UnitSpec with OneInstancePerTest {
  private val store: DataStore = new InMemoryStore
  private val service          = new SimpleAccountService(store)

  "SimpleAccountService" - {

    "deposit should" - {

      "add money to an existing account" in {
        val originalBalance = money(100)
        val depositedMoney  = money(50)
        val expectedBalance = originalBalance plus depositedMoney
        val account         = createAccountWith(originalBalance.getAmount)

        store.read(account.id).futureValue.balance shouldBe originalBalance
        service.deposit(depositedMoney, account.id).futureValue shouldBe account.copy(balance = expectedBalance)
        store.read(account.id).futureValue.balance shouldBe expectedBalance
      }

      "return error for non existing account" in {
        whenReady(service.deposit(money(100), randomAccount().id).failed) { e =>
          e shouldBe an[AccountNotFoundException]
        }
      }

      "forbid negative amount operation" in {
        val account = createAccountWith(10)
        whenReady(service.deposit(money(-10), account.id).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

    }

    "withdraw should" - {

      "withdraw money from an existing account" in {
        val originalBalance = money(100)
        val withdrawnMoney  = money(40)
        val expectedBalance = originalBalance minus withdrawnMoney
        val account         = createAccountWith(originalBalance.getAmount)

        store.read(account.id).futureValue.balance shouldBe originalBalance
        service.withdraw(withdrawnMoney, account.id).futureValue shouldBe account.copy(balance = expectedBalance)
        store.read(account.id).futureValue.balance shouldBe expectedBalance
      }

      "return error for non existing account" in {
        whenReady(service.withdraw(money(10), randomAccount().id).failed) { e =>
          e shouldBe an[AccountNotFoundException]
        }
      }

      "forbid negative amount operation" in {
        val account = createAccountWith(10)
        whenReady(service.withdraw(money(-10), account.id).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

      "forbid overdraft" in {
        val account = createAccountWith(10)
        whenReady(service.withdraw(money(100), account.id).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }
    }

    "transfer should" - {
      "withdraw from source account and deposit to destination account" in {
        val sourceAccount              = createAccountWith(100)
        val destinationAccount         = createAccountWith(0)
        val transferAmount             = money(50)
        val expectedSourceBalance      = sourceAccount.balance minus transferAmount
        val expectedDestinationBalance = destinationAccount.balance plus transferAmount

        store.read(sourceAccount.id).futureValue.balance shouldBe sourceAccount.balance
        store.read(destinationAccount.id).futureValue.balance shouldBe destinationAccount.balance
        service.transfer(transferAmount, sourceAccount.id, destinationAccount.id).futureValue shouldBe
          (sourceAccount.copy(balance = expectedSourceBalance), destinationAccount.copy(balance = expectedDestinationBalance))
        store.read(sourceAccount.id).futureValue.balance shouldBe expectedSourceBalance
        store.read(destinationAccount.id).futureValue.balance shouldBe expectedDestinationBalance
      }

      "return error for non existing account" in {
        whenReady(service.transfer(money(10), randomAccount().id, randomAccount().id).failed) { e =>
          e shouldBe an[AccountNotFoundException]
        }
      }

      "forbid negative amount operation" in {
        val sourceAccount      = createAccountWith(10)
        val destinationAccount = createAccountWith(10)

        whenReady(service.transfer(money(-10), sourceAccount.id, destinationAccount.id).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

      "forbid overdraft" in {
        val sourceAccount      = createAccountWith(10)
        val destinationAccount = createAccountWith(10)

        whenReady(service.transfer(money(100), sourceAccount.id, destinationAccount.id).failed) { e =>
          e shouldBe an[IllegalArgumentException]
        }
      }

      "rollback source account withdrawal on failed destination account deposit" in {
        val sourceAccount      = createAccountWith(100)
        val destinationAccount = randomAccount()

        whenReady(service.transfer(money(50), sourceAccount.id, destinationAccount.id).failed) { e =>
          e shouldBe an[AccountNotFoundException]
        }
        store.read(sourceAccount.id).futureValue.balance shouldBe sourceAccount.balance
      }
    }

    "simple concurrency is not broken test" in {
      val service       = new SimpleAccountService(new InMemoryStore)
      implicit val ec   = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))
      val testUnit      = 5000
      val sourceAccount = createAccountWith(testUnit)
      val targetAccount = createAccountWith(testUnit)

      def spawnTransfers(result: List[Future[Unit]], times: Int): List[Future[Unit]] = {
        if (times == 0) result
        else {
          val iteration = List(
            service.transfer(money(1), sourceAccount.id, targetAccount.id).map(_ => ()),
            service.transfer(money(1), targetAccount.id, sourceAccount.id).map(_ => ())
          )
          spawnTransfers(result ::: iteration, times - 1)
        }
      }

      /*
        We perform 5000 concurrent A -> B, B -> A transfers with amount 1
        That must produce same balances after execution
       */
      Future.sequence(spawnTransfers(Nil, testUnit)).futureValue

      service.read(sourceAccount.id).futureValue shouldBe sourceAccount
      service.read(targetAccount.id).futureValue.balance shouldBe targetAccount
    }

  }

  private def createAccountWith(amount: BigDecimal): Account = {
    val account = accountWith(amount)
    store.create(account.id).futureValue
    store.add(account.balance.toTransaction, account.id).futureValue
    account
  }

  private def createAccount(): Account = {
    val account = randomAccount()
    store.create(account.id).futureValue
  }

}
