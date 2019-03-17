package com.oef.bank.account.infrastructure.outbound.store.db

import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.provided.DataStore
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

  /** Update an existing account.
    *
    * @param account new account details.
    * @return - account state before the update;
    *         - error if account not found.
    * */
  override def update(account: Account): Future[AccountBeforeUpdate] = ???

  /** Delete an account.
    *
    * @param accountWith details fo the account to be deleted.
    * @return - deleted account if account existed;
    *         - error if account didn't exist.
    * */
  override def delete(accountWith: AccountId): Future[DeletedAccount] = ???
}
