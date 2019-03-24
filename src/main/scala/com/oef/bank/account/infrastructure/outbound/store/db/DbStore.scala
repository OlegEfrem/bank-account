package com.oef.bank.account.infrastructure.outbound.store.db

import com.oef.bank.account.domain.model.{Account, AccountId, Transaction}
import com.oef.bank.account.domain.service.provided.DataStore
import com.oef.bank.account.domain.model.AccountNotFoundException
import scala.concurrent.Future

class DbStore extends DataStore {

  /** Create a new account with zero money.
    *
    * @param accountWith details of the account to be created.
    * @return - the newly created account with zero money;
    *         - error if account exists.
    * */
  override def create(accountWith: AccountId): Future[Account] = ???

  /** Read account details.
    *
    * @param accountBy account sort code and number to read details for.
    * @return - the read account if found;
    *         - error if account not found.
    * */
  override def read(accountBy: AccountId): Future[Account] = ???

  /** Delete an account.
    *
    * @param accountWith details fo the account to be deleted.
    * @return - deleted account if account existed;
    *         - error if account didn't exist.
    * */
  override def delete(accountWith: AccountId): Future[DeletedAccount] = ???

  /** Adds a new transaction to an existing account.
    *
    * @param transaction the new transaction to be added.
    * @param to account to add new transaction to.
    * @return - new balance after adding the new transaction;
    *         - [[AccountNotFoundException]] if account not found.
    * */
  override def add(transaction: Transaction, to: AccountId): Future[NewBalance] = ???

  /** Adds a new transaction to an existing account.
    *
    * @param by account for which to return the transactions.
    * @return - transactions for the requested account;
    *         - [[AccountNotFoundException]] if account not found.
    * */
  override def readTransactions(by: AccountId): Future[List[Transaction]] = ???

  /** Retrieves balance for given account id.
    *
    * @param id account for which to return the balance.
    * @return Future completed with:
    *         - balance for requested account;
    *         - [[AccountNotFoundException]] if account not found.
    * */
  override def balanceFor(id: AccountId): Future[NewBalance] = ???
}
