package com.oef.bank.account.domain.model

import java.time.Instant
import java.util.UUID

/** Class to store transaction details
  * @param value should be a positive number for deposits, negative number for withdrawals.
  * @param time time when transaction happens, defaults to time when this object was created.
  * */
case class Transaction(value: BigDecimal, time: Instant = Instant.now, private val uuid: UUID = UUID.randomUUID()) {
  def id: UUID = uuid
  value.setScale(2, BigDecimal.RoundingMode.HALF_EVEN)
}
