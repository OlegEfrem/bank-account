package com.oef.bank.account.domain.model
import com.oef.bank.UnitSpec
import org.joda.money.{CurrencyMismatchException, CurrencyUnit, Money}

class AccountTest extends UnitSpec {

  "Account " - {
    val gbp = Money.of(CurrencyUnit.GBP, 25.12)
    val usd = Money.of(CurrencyUnit.USD, 12.05)
    val gbpAccount = Account(AccountId(123, 123456), gbp)

      "deposit should" - {
        "allow same currency operation" in {
          gbpAccount deposit gbp shouldBe gbpAccount.copy(balance = gbpAccount.balance plus gbp)
        }

        "forbid different currency operation" in {
          a[CurrencyMismatchException] shouldBe thrownBy {gbpAccount deposit usd}
        }

        "forbid negative amount operation" in {
          a[IllegalArgumentException] shouldBe thrownBy {gbpAccount deposit gbp.negated()}
        }
      }

      "withdraw should" - {
        "allow same currency operation" in {
          gbpAccount withdraw gbp shouldBe gbpAccount.copy(balance = gbpAccount.balance minus gbp)
        }

        "forbid different currency operation" in {
          a[CurrencyMismatchException] shouldBe thrownBy {gbpAccount withdraw usd}
        }

        "forbid negative amount operation" in {
          a[IllegalArgumentException] shouldBe thrownBy {gbpAccount withdraw gbp.negated()}
        }

        "forbid overdraft" in {
          a[IllegalArgumentException] shouldBe thrownBy {gbpAccount withdraw gbpAccount.balance.multipliedBy(2)}
        }
      }

    }
}
