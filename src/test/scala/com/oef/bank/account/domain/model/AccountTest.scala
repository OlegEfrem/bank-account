package com.oef.bank.account.domain.model
import com.oef.bank.UnitSpec
import org.joda.money.{CurrencyMismatchException, CurrencyUnit, IllegalCurrencyException, Money}

class AccountTest extends UnitSpec {

  "Account " - {
    val amount     = 25.12
    val gbp        = Money.of(CurrencyUnit.GBP, amount)
    val usd        = Money.of(CurrencyUnit.USD, 12.05)
    val id         = AccountId(123, 123456, CurrencyUnit.GBP)
    val gbpAccount = Account(id, gbp)

    "plus (+) should" - {
      "allow same currency operation" in {
        gbpAccount + gbp shouldBe gbpAccount.copy(balance = gbpAccount.balance plus gbp)
      }

      "forbid different currency operation" in {
        a[CurrencyMismatchException] shouldBe thrownBy { gbpAccount + usd }
      }

    }

    "minus (-) should" - {
      "allow same currency operation" in {
        gbpAccount - gbp shouldBe gbpAccount.copy(balance = gbpAccount.balance minus gbp)
      }

      "forbid different currency operation" in {
        a[CurrencyMismatchException] shouldBe thrownBy { gbpAccount - usd }
      }

    }

    "apply should" - {
      "create account with money for existing currency" in {
        Account(id, "GBP", gbp.getAmount) shouldBe Account(id, gbp)
      }

      "forbid creating account with non existing currency" in {
        an[IllegalCurrencyException] shouldBe thrownBy(Account(id, "NonExistingCurrency"))
      }
    }

  }
}
