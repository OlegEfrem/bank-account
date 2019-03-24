package com.oef.bank.account.domain.model

sealed abstract class BankAccountException(message: String, exception: Exception) extends Exception(message, exception)

//scalastyle:off
case class AccountNotFoundException(message: String, exception: Exception = null) extends BankAccountException(message, exception)

case class AccountAlreadyExistsException(message: String, exception: Exception = null) extends BankAccountException(message, exception)

//scalastyle:on
