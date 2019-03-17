package com.oef.bank.account.infrastructure.inbound.http

case class ConversionRequest(fromCurrency: String, toCurrency: String, amount: BigDecimal)

case class ConversionResponse(exchange: BigDecimal, amount: BigDecimal, original: BigDecimal)
