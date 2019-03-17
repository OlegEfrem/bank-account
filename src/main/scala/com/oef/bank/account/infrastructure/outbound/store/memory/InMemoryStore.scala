package com.oef.bank.account.infrastructure.outbound.store.memory

import java.util.concurrent.ConcurrentHashMap

import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.provided.DataStore

import scala.concurrent.Future
import scala.collection._
import JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class InMemoryStore extends DataStore {
  private val accounts: concurrent.Map[AccountId, Account] = new ConcurrentHashMap[AccountId, Account]().asScala

  /** Create a new account with zero money.
    *
    * @param accountWith details of the account to be created.
    * @return - the newly created account with zero money;
    *         - error if account exists.
    * */
  override def create(accountWith: AccountId): Future[Account] = {
    val account = Account(accountWith)
    Future.fromTry(
      accounts.putIfAbsent(accountWith, account) match {
        case None    => Success(account)
        case Some(x) => Failure(new IllegalArgumentException(s"account exists already: $accountWith"))
      }
    )
  }

  /** Read account details.
    *
    * @param accountBy account sort code and number to read details for.
    * @return - the account if found;
    *         - error if account not found.
    * */
  override def read(accountBy: AccountId): Future[Account] = {
    Future {
      accounts.getOrElse(accountBy, throw new IllegalArgumentException(s"account doesn't exist: $accountBy"))
    }
  }

  /** Update an existing account.
    *
    * @param account new account details.
    * @return - account state before the update;
    *         - error if account not found.
    * */
  override def update(account: Account): Future[AccountBeforeUpdate] = {
    Future.fromTry(
      accounts.replace(account.id, account) match {
        case Some(x) => Success(x)
        case None    => Failure(new IllegalArgumentException(s"account doesn't exist: ${account.id}"))
      }
    )
  }

  /** Delete an account.
    * @param accountWith details fo the account to be deleted.
    * @return - deleted account if account existed;
    *         - error if account didn't exist.
    * */
  override def delete(accountWith: AccountId): Future[DeletedAccount] = {
    Future.fromTry(
      accounts.remove(accountWith) match {
        case None    => Failure(new IllegalArgumentException(s"account doesn't exist: $accountWith"))
        case Some(x) => Success(x)
      }
    )
  }

}
