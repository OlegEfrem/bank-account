package com.oef.bank.account.domain.model
import com.oef.bank.UnitSpec
import org.joda.money.{CurrencyMismatchException, CurrencyUnit, IllegalCurrencyException, Money}

class AccountTest extends UnitSpec {

  "Account " - {
    val amount     = 25.12
    val gbp        = Money.of(CurrencyUnit.GBP, amount)
    val usd        = Money.of(CurrencyUnit.USD, 12.05)
    val id         = AccountId(123, 123456)
    val gbpAccount = Account(id, gbp)

    "deposit should" - {
      "allow same currency operation" in {
        gbpAccount deposit gbp shouldBe gbpAccount.copy(balance = gbpAccount.balance plus gbp)
      }

      "forbid different currency operation" in {
        a[CurrencyMismatchException] shouldBe thrownBy { gbpAccount deposit usd }
      }

      "forbid negative amount operation" in {
        a[IllegalArgumentException] shouldBe thrownBy { gbpAccount deposit gbp.negated() }
      }
    }

    "withdraw should" - {
      "allow same currency operation" in {
        gbpAccount withdraw gbp shouldBe gbpAccount.copy(balance = gbpAccount.balance minus gbp)
      }

      "forbid different currency operation" in {
        a[CurrencyMismatchException] shouldBe thrownBy { gbpAccount withdraw usd }
      }

      "forbid negative amount operation" in {
        a[IllegalArgumentException] shouldBe thrownBy { gbpAccount withdraw gbp.negated() }
      }

      "forbid overdraft" in {
        a[IllegalArgumentException] shouldBe thrownBy { gbpAccount withdraw gbpAccount.balance.multipliedBy(2) }
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
