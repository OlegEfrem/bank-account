package com.oef.bank.account.domain.service

import com.oef.bank.account.domain.model.{Account, AccountId, Transaction}
import com.oef.bank.account.domain.service.implementations.SimpleAccountService
import com.oef.bank.account.domain.service.provided.DataStore
import org.joda.money.Money

import scala.concurrent.Future

trait AccountService {
  protected val store: DataStore
  type FromAccount = Account
  type ToAccount   = Account

  /** Deposit money to the account.
    * @param money currency and amount to be deposited, amount be a positive number.
    * @param to the account to which to deposit the money.
    * @return Future completes with:
    *         - the new account state;
    *         - error if account not found or money has a negative amount;
    * */
  def deposit(money: Money, to: AccountId): Future[Account]

  /** Withdraw money from the account.
    * @param money currency and amount to be withdrawn, amount must be a positive number.
    * @param from the account from which to withdraw the money.
    * @return Future completes with:
    *         - the new account state;
    *         - error if account not found, money has a negative amount or overdraft attempted.
    * */
  def withdraw(money: Money, from: AccountId): Future[Account]

  /** Transfer money from one account to another.
    * @param money amount to be transferred.
    * @param from account from which to withdraw money.
    * @param to account to which to deposit money.
    * @return Future completes with:
    *         - new state of from/to accounts;
    *         - error if there are missing accounts, currency mismatches, negative amounts provided or overdraft attempted.
    * */
  def transfer(money: Money, from: AccountId, to: AccountId): Future[(FromAccount, ToAccount)]

  // functions exposed from the DataStore:
  /** Create a new account with zero money.
    * @param accountWith details of the account to be created.
    * @return Future completes with:
    *         - the newly created account with zero money;
    *         - error if account exists.
    * */
  def create(accountWith: AccountId): Future[Account] = store.create(accountWith)

  /** Read account details.
    * @param accountBy account sort code and number to read details for.
    * @return Future completes with:
    *         - the read account if found;
    *         - error if account not found.
    * */
  def read(accountBy: AccountId): Future[Account] = store.read(accountBy)

  /** Delete an account.
    * @param accountWith details fo the account to be deleted.
    * @return Future completes with:
    *         - deleted account if account existed;
    *         - error if account didn't exist.
    * */
  def delete(accountWith: AccountId): Future[Account] = store.delete(accountWith)

}

object AccountService {
  def apply(dataStore: DataStore): AccountService = new SimpleAccountService(dataStore)
}
