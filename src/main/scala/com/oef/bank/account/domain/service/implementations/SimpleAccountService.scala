package com.oef.bank.account.domain.service.implementations

import com.oef.bank.account.domain.model.AccountId
import com.oef.bank.account.domain.service.AccountService
import com.oef.bank.account.domain.service.provided.DataStore
import org.joda.money.Money
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

class SimpleAccountService(override protected val store: DataStore) extends AccountService {

  /** Deposit money to the account.
    *
    * @param money currency and amount to be deposited, amount be a positive number.
    * @param to the account to which to deposit the money.
    * @return - the new account state;
    *         - error if account not found or money has a negative amount;
    * */
  override def deposit(money: Money, to: AccountId): Future[ToAccount] = {
    for {
      _      <- requirePositive(money)
      acc    <- store.read(to)
      newAcc = acc + money
      _      <- store.update(newAcc)
    } yield newAcc
  }

  /** Withdraw money from the account.
    *
    * @param money currency and amount to be withdrawn, amount must be a positive number.
    * @param from the account from which to withdraw the money.
    * @return - the new account state;
    *         - error if account not found, money has a negative amount or overdraft attempted.
    * */
  override def withdraw(money: Money, from: AccountId): Future[ToAccount] = {
    for {
      _      <- requirePositive(money)
      acc    <- store.read(from)
      _      <- forbidOverdraft(acc.balance, money)
      newAcc = acc - money
      _      <- store.update(newAcc)
    } yield newAcc

  }

  private def requirePositive(money: Money): Future[Unit] = {
    Future.fromTry(
      Try {
        require(money.isPositive, s"negative amount forbidden: $money")
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
