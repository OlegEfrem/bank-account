package com.oef.bank.account.domain.service.implementations

import com.oef.bank.account.domain.model.AccountId
import com.oef.bank.account.domain.service.AccountService
import com.oef.bank.account.domain.service.provided.DataStore
import org.joda.money.Money
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
      acc <- store.read(to)
      newAcc = acc.deposit(money)
      _ <- store.update(newAcc)
    } yield newAcc
  }

  /** Withdraw money from the account.
    *
    * @param money currency and amount to be withdrawn, amount must be a positive number.
    * @param from the account from which to withdraw the money.
    * @return - the new account state;
    *         - error if account not found, money has a negative amount or overdraft attempted.
    * */
  override def withdraw(money: Money, from: AccountId): Future[ToAccount] = ???
}
