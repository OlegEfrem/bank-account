package com.oef.bank.account.domain.service.provided

import com.oef.bank.account.domain.model.{Account, AccountId}
import scala.concurrent.Future

/** Trait for data store implementations to be provided by the infrastructure layer.
  * */
trait DataStore {

  /** Create a new account with zero money.
    * @param accountId details of the account to be created.
    * @return - the newly created account with zero money;
    *         - error if account exists.
    * */
  def create(accountId: AccountId): Future[Account]

  /** Read account details.
    * @param accountBy account sort code and number to read details for.
    * @return - the read account if found;
    *         - error if account not found.
    * */
  def read(accountBy: AccountId): Future[Account]

  /** Delete an account.
    * @param accountWith details fo the account to be deleted.
    * @return - success if account exists;
    *         - error if account doesn't exist.
    * */
  def delete(accountWith: AccountId): Future[Unit]

}
