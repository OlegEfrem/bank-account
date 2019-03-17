package com.oef.bank.account.domain.service

import com.oef.bank.account.domain.model.{Account, AccountId}
import org.joda.money.Money
import scala.concurrent.Future

trait AccountService {
  type FromAccount = Account
  type ToAccount = Account

  /** Create a new account.
    * @param account details of the account to be created.
    * @return - the newly created account;
    *         - error if account exists or negative amount provided.
    * */
  def create(account: Account): Future[Account]

  /** Read account details.
    * @param accountBy account sort code and number to read details for.
    * @return - the read account if found;
    *         - error if account not found.
    * */
  def read(accountBy: AccountId): Future[Account]

  /** Deposit money to the account.
    * @param money amount to be deposited.
    * @param to the account to which to deposit the money.
    * @return - the new account state;
    *         - error if account not found;
    * */
  def deposit(money: Money, to: AccountId): Future[Account]

  /** Withdraw money from the account.
    * @param money amount to be withdrawn.
    * @param from the account from which to withdraw the money.
    * @return - the new account state;
    *         - error if account not found or overdraft attempted.
    * */
  def withdraw(money: Money, from: AccountId): Future[Account]

  /** Transfer money from one account to another.
    * @param money amount to be transferred.
    * @param from account from which to withdraw money.
    * @param to account to whith to deposit money.
    * @return - new state of from/to accounts;
    *         - error if there are currency mismatches, negative amounts provided or overdraft attempted.
    * */
  def transfer(money: Money, from: AccountId, to: AccountId): Future[(FromAccount, ToAccount)] = {
    for {
      fromAcc <- read(from)
      toAcc <- read(to)
      newFromAcc <- fromAcc.withdraw(money)
      newToAcc <- toAcc.deposit(money)
    } yield (newFromAcc, newToAcc)
  }

  /** Delete an account.
    * @param accountWith details fo the account to be deleted.
    * @return success, even if the account previously didn't exist.
    *         Done in this way to avoid reading an account before deleting it.
    * */
  def delete(accountWith: AccountId): Future[Unit]

}
