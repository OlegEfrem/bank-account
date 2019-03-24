package com.oef.bank.account.infrastructure.outbound.store.db

import com.oef.bank.account.domain.service.provided.DataStore
import com.oef.bank.account.domain.model._
import scala.concurrent.Future

class DbStore extends DataStore {

  override def create(accountWith: AccountId): Future[Account] = ???

  override def read(accountBy: AccountId): Future[Account] = ???

  override def delete(accountWith: AccountId): Future[DeletedAccount] = ???

  override def add(transaction: Transaction, to: AccountId): Future[NewBalance] = ???

  override def readTransactions(by: AccountId): Future[List[Transaction]] = ???

  override def balanceFor(id: AccountId): Future[NewBalance] = ???
}
