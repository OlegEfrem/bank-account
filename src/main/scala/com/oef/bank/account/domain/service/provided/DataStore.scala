package com.oef.bank.account.domain.service.provided

import com.oef.bank.account.domain.model._
import org.joda.money.Money
import scala.concurrent.Future

/** Trait for data store implementations to be provided by the infrastructure layer.
  * */
trait DataStore {
  type DeletedAccount      = Account
  type AccountBeforeUpdate = Account
  type NewBalance          = Money

  /** Create a new account with zero money.
    * @param accountWith details of the account to be created.
    * @return Future completed with:
    *         - the newly created account with zero money;
    *         - AccountAlreadyExistsException if account exists.
    * */
  def create(accountWith: AccountId): Future[Account]

  /** Read account details.
    * @param accountBy account sort code and number to read details for.
    * @return Future completed with:
    *         - the read account if found;
    *         - AccountNotFoundException if account not found.
    * */
  def read(accountBy: AccountId): Future[Account]

  /** Adds a new transaction to an existing account.
    * @param transaction the new transaction to be added.
    * @param to account to add new transaction to.
    * @return Future completed with:
    *         - new balance after adding the new transaction;
    *         - AccountNotFoundException if account not found.
    * */
  def add(transaction: Transaction, to: AccountId): Future[NewBalance]

  /** Rollbacks a transaction when needed (in case of a failure for example).
    * @param transaction transaction to be rollback-ed.
    * @param from which account transactions belongs to.
    * @return Future completed with:
    *         - Success in case of a successful rollback;
    *         - Failure in case of an unsuccessful rollback or a non existing account.
    * */
  def remove(transaction: Transaction, from: AccountId): Future[Unit]

  /** Reads transactions of the given account.
    * @param by account for which to read transactions.
    * @return Future completes with:
    *         - list of transactions with required account;
    *         - AccountNotFoundException if account not found.
    * */
  def readTransactions(by: AccountId): Future[List[Transaction]]

  /** Retrieves balance for given account id.
    * @param id account for which to return the balance.
    * @return Future completed with:
    *         - balance for requested account;
    *         - AccountNotFoundException if account not found.
    * */
  def balanceFor(id: AccountId): Future[Money]

  /** Delete an account.
    * @param accountWith details fo the account to be deleted.
    * @return Future completed with:
    *         - deleted account if account existed;
    *         - AccountNotFoundException if account didn't exist.
    * */
  def delete(accountWith: AccountId): Future[DeletedAccount]

}
