package com.oef.bank.account.infrastructure.outbound.store.memory

import java.util.concurrent.ConcurrentHashMap
import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.provided.DataStore
import scala.concurrent.Future

object InMemoryStore extends DataStore {
  private val accounts = new ConcurrentHashMap[AccountId, Account]()

  /** Create a new account with zero money.
    *
    * @param accountId details of the account to be created.
    * @return - the newly created account with zero money;
    *         - error if account exists.
    * */
  override def create(accountId: AccountId): Future[Account] = ???

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
    * @return - success if account exists;
    *         - error if account doesn't exist.
    * */
  override def delete(accountWith: AccountId): Future[Unit] = ???
}
