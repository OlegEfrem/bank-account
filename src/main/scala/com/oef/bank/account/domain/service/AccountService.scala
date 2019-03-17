package com.oef.bank.account.domain.service

import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.provided.DataStore
import org.joda.money.Money
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AccountService {
  protected val store: DataStore
  type FromAccount = Account
  type ToAccount   = Account

  /** Deposit money to the account.
    * @param money currency and amount to be deposited, amount be a positive number.
    * @param to the account to which to deposit the money.
    * @return - the new account state;
    *         - error if account not found or money has a negative amount;
    * */
  def deposit(money: Money, to: AccountId): Future[Account]

  /** Withdraw money from the account.
    * @param money currency and amount to be withdrawn, amount must be a positive number.
    * @param from the account from which to withdraw the money.
    * @return - the new account state;
    *         - error if account not found, money has a negative amount or overdraft attempted.
    * */
  def withdraw(money: Money, from: AccountId): Future[Account]

  /** Transfer money from one account to another.
    * Note this is a rich interface function, defined only in terms of other functions of the same trait.
    * @param money amount to be transferred.
    * @param from account from which to withdraw money.
    * @param to account to whith to deposit money.
    * @return - new state of from/to accounts;
    *         - error if there are currency mismatches, negative amounts provided or overdraft attempted.
    * */
  def transfer(money: Money, from: AccountId, to: AccountId): Future[(FromAccount, ToAccount)] = {
    for {
      fromAcc <- store.read(from)
      toAcc   <- store.read(to)
    } yield (fromAcc.withdraw(money), toAcc.deposit(money))
  }

}
