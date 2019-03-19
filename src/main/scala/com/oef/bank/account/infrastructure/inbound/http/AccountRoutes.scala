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

  val endpoints: Route = pathPrefix("account") {
    managementRoutes() ~ transferRoutes()
  }

  private def managementRoutes(): Route = {
    pathEndOrSingleSlash {
      parameters("sort-code", "acc-no") { (sortCode, accNo) =>
        get {
          retrieveAccount(sortCode, accNo)
        }
      } ~
        put {
          entity(as[String]) { accId =>
            createAccount(jsonConverter.fromJson[AccountId](accId))
          }
        } ~
        delete {
          entity(as[String]) { accId =>
            deleteAccount(jsonConverter.fromJson[AccountId](accId))
          }
        }
    }
  }

  private def transferRoutes(): Route = {
    concat(
      path("deposit") {
        post {
          entity(as[String]) { details =>
            deposit(details)
          }
        }
      },
      path("withdrawal") {
        post {
          entity(as[String]) { details =>
            withdraw(details)
          }
        }
      },
      path("transfer") {
        post {
          entity(as[String]) { details =>
            transfer(details)
          }
        }
      }
    )
  }

  private def deposit(details: String): Route = {
    val depositDetails = jsonConverter.fromJson[ApiDeposit](details)
    val result         = service.deposit(depositDetails.money.toDomain, depositDetails.to)
    createResponse(result)
  }

  private def withdraw(details: String): Route = {
    val withdrawDetails = jsonConverter.fromJson[ApiWithdraw](details)
    val result          = service.withdraw(withdrawDetails.money.toDomain, withdrawDetails.from)
    createResponse(result)
  }

  private def transfer(details: String): Route = {
    val transferDetails = jsonConverter.fromJson[ApiTransfer](details)
    val result          = service.transfer(transferDetails.money.toDomain, transferDetails.from, transferDetails.to)
    onComplete(result) {
      case Success(accounts) => complete(jsonConverter.toJson(Map("fromAccount" -> ApiAccount(accounts._1), "toAccount" -> ApiAccount(accounts._2))))
      case Failure(err)      => completeWithError(err)
    }
  }

  def createAccount(id: AccountId): Route = {
    val result = service.create(id)
    createResponse(result, Created)
  }

  def retrieveAccount(sortCode: String, accNo: String): Route = {
    Try {
      AccountId(sortCode.toInt, accNo.toLong)
    } match {
      case Success(accId) =>
        val result = service.read(accId)
        createResponse(result)
      case Failure(err) => complete(BadRequest, s"invalid sort code: $sortCode or account number: $accNo, error: ${err.getMessage}")
    }

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
