package com.oef.bank.account.infrastructure.inbound.http

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.oef.bank.account.domain.model.{Account, AccountId}
import com.oef.bank.account.domain.service.AccountService
import com.oef.bank.account.infrastructure.json.JsonConverter
import akka.http.scaladsl.model.StatusCodes._
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

class AccountRoutes(service: AccountService, jsonConverter: JsonConverter) {

  val endpoints: Route = pathPrefix("account" / "sort-code" / Segment / "acc-no" / Segment) { (sortCode, accNo) =>
    Try {
      AccountId(sortCode.toInt, accNo.toLong)
    } match {
      case Success(x)   => managementRoutes(x) ~ transferRoutes(x)
      case Failure(err) => complete(BadRequest, s"invalid sort code: $sortCode or account number: $accNo, error: ${err.getMessage}")
    }
  }

  private def managementRoutes(id: AccountId): Route = {
    pathEndOrSingleSlash {
      get {
        retrieveAccount(id)
      } ~ put {
        createAccount(id)
      } ~ delete {
        deleteAccount(id)
      }
    }
  }

  private def transferRoutes(id: AccountId): Route = {
    concat(
      path("deposit") {
        post {
          entity(as[String]) { money =>
            deposit(money, id)
          }
        }
      },
      path("withdraw") {
        post {
          entity(as[String]) { money =>
            withdraw(money, id)
          }
        }
      },
      path("transfer") {
        post {
          entity(as[String]) { details =>
            transfer(details, id)
          }
        }
      }
    )
  }

  private def deposit(moneyJson: String, id: AccountId): Route = {
    val apiMoney = jsonConverter.fromJson[ApiMoney](moneyJson)
    val result   = service.deposit(apiMoney.toDomain, id)
    createResponse(result)
  }

  private def withdraw(moneyJson: String, id: AccountId): Route = {
    val apiMoney = jsonConverter.fromJson[ApiMoney](moneyJson)
    val result   = service.withdraw(apiMoney.toDomain, id)
    createResponse(result)
  }

  private def transfer(details: String, id: AccountId): Route = {
    val transfer = jsonConverter.fromJson[Transfer](details)
    val result   = service.transfer(transfer.money.toDomain, id, transfer.to)
    onComplete(result) {
      case Success(accounts) => complete(jsonConverter.toJson(Map("fromAccount" -> ApiAccount(accounts._1), "toAccount" -> ApiAccount(accounts._2))))
      case Failure(err)      => completeWithError(err)
    }
  }

  def createAccount(id: AccountId): Route = {
    val result = service.create(id)
    createResponse(result, Created)
  }

  def retrieveAccount(id: AccountId): Route = {
    val result = service.read(id)
    createResponse(result)
  }

  def deleteAccount(id: AccountId): Route = {
    val result = service.delete(id)
    createResponse(result)
  }

  private def createResponse(result: Future[Account], successCode: StatusCode = OK) = {
    onComplete(result) {
      case Success(account) => complete(successCode, jsonConverter.toJson(ApiAccount(account)))
      case Failure(err)     => completeWithError(err)
    }
  }
  private def completeWithError(exception: Throwable): Route = {
    import StatusCodes._
    exception match {
      case e: IllegalArgumentException => complete(BadRequest, e.getMessage)
      case _                           => complete(InternalServerError, exception.getMessage)
    }
  }

}
