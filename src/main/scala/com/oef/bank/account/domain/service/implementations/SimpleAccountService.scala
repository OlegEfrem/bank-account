package com.oef.bank.account.domain.service.implementations

import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.{AccountService, Implicits}
import com.oef.bank.account.domain.service.provided.DataStore
import org.joda.money.Money
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

class SimpleAccountService(override protected val store: DataStore) extends AccountService with Implicits {

  override def deposit(money: Money, to: AccountId): Future[ToAccount] = {
    for {
      _          <- checkRequirements(money, to)
      newBalance <- store.add(money.toTransaction, to)
    } yield Account(to, newBalance)
  }

  override def withdraw(money: Money, from: AccountId): Future[Account] = {
    for {
      _          <- checkRequirements(money, from)
      balance    <- store.balanceFor(from)
      _          <- forbidOverdraft(balance, money)
      newBalance <- store.add(money.negated().toTransaction, from)
    } yield Account(from, newBalance)

  }

  private def checkRequirements(money: Money, accountId: AccountId): Future[Unit] = {
    for {
      _ <- requireSameCurrencies(money, accountId)
      _ <- requirePositive(money)
    } yield ()
  }

  private def requirePositive(money: Money): Future[Unit] = {
    Future.fromTry(
      Try {
        require(money.isPositive, s"negative amount forbidden: $money")
      }
    )
  }

  private def requireSameCurrencies(money: Money, accountId: AccountId): Future[Unit] = {
    Future.fromTry(
      Try {
        require(money.getCurrencyUnit.getCode == accountId.currencyUnit.getCode,
                s"account: $accountId doesn't not support currency: ${money.getCurrencyUnit.getCode} ")
      }
    )
  }

  private def forbidOverdraft(balance: Money, toSubtract: Money): Future[Unit] = {
    Future.fromTry(
      Try {
        require(balance.minus(toSubtract).isPositiveOrZero, s"insufficient funds: available $balance, required $toSubtract")
      }
    )
  }

}
