package com.oef.bank.account.infrastructure.outbound.store.memory

import java.util.concurrent.ConcurrentHashMap
import com.oef.bank.account.domain.model._
import com.oef.bank.account.domain.service.provided.DataStore
import org.joda.money.Money
import scala.concurrent.Future
import scala.collection._
import JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

class InMemoryStore extends DataStore {
  private val transactions: concurrent.Map[AccountId, ListBuffer[Transaction]] = new ConcurrentHashMap[AccountId, ListBuffer[Transaction]]().asScala

  override def create(accountWith: AccountId): Future[Account] = {
    Future.fromTry(
      transactions.putIfAbsent(accountWith, ListBuffer()) match {
        case None    => Success(assembleAccount(accountWith, List()))
        case Some(x) => Failure(AccountAlreadyExistsException(s"account exists already: $accountWith"))
      }
    )
  }

  override def read(accountBy: AccountId): Future[Account] = {
    balanceFor(accountBy).map(Account(accountBy, _))
  }

  override def delete(accountWith: AccountId): Future[DeletedAccount] = {
    Future.fromTry(
      transactions.remove(accountWith) match {
        case None      => notFound(accountWith)
        case Some(txs) => Success(assembleAccount(accountWith, txs.toList))
      }
    )
  }

  override def add(transaction: Transaction, to: AccountId): Future[NewBalance] = {
    Future.fromTry(
      transactions.get(to) match {
        case Some(txs) =>
          val newTxs = txs += transaction
          Success(Money.of(to.currencyUnit, sum(newTxs.toList)))
        case None => notFound(to)
      }
    )
  }

  def balanceFor(id: AccountId): Future[Money] = {
    readTransactions(id).map(txs => Money.of(id.currencyUnit, sum(txs)))
  }

  override def readTransactions(by: AccountId): Future[List[Transaction]] = {
    Future.fromTry(
      transactions.get(by) match {
        case Some(txs) => Success(txs.toList)
        case None      => notFound(by)
      }
    )
  }

  private def assembleAccount(id: AccountId, transactions: List[Transaction]): Account = {
    Account(id, Money.of(id.currencyUnit, sum(transactions)))
  }

  private def sum(transactions: List[Transaction]): java.math.BigDecimal = {
    transactions.view.map(_.value).sum.underlying()
  }
  private def notFound(accountId: AccountId): Try[Nothing] = {
    Failure(AccountNotFoundException(s"account doesn't exist: $accountId"))
  }

}
