package io.github.mr_tolmach.generators

import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import io.github.mr_tolmach.metadata.RegionMetadataProvider
import io.github.mr_tolmach.metadata.model.{PhoneNumberTypes, RegionMetadata, Regions}
import org.scalacheck.ShrinkLowPriority
import org.scalactic.anyvals.{PosInt, PosZDouble}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class GeneratorSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with ShrinkLowPriority {

  override implicit val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(
    minSuccessful = PosInt(100000),
    maxDiscardedFactor = PosZDouble(0.0000001),
    workers = PosInt(30)
  )

  private def checkPhoneNumber(phoneNumber: String): Unit = {
    val parsed = PhoneNumberHelper.parseValidPhoneNumber(phoneNumber)
    val actual = PhoneNumberHelper.Util.format(parsed, PhoneNumberFormat.E164)
    actual shouldBe phoneNumber
  }

  private def checkPhoneNumber(regionMetadata: RegionMetadata)(phoneNumber: String): Unit = {
    checkNumberWithRegion(regionMetadata)(phoneNumber)
    checkNumberWithoutRegion(regionMetadata)(phoneNumber)
  }

  private def checkNumberWithRegion(regionMetadata: RegionMetadata)(phoneNumber: String): Unit = {
    val parsed = PhoneNumberHelper.parseValidPhoneNumber(phoneNumber, regionMetadata.region)
    regionMetadata.countryCodeToTypePatterns.keys should contain(parsed.getCountryCode)
    val actual = PhoneNumberHelper.Util.format(parsed, PhoneNumberFormat.E164)
    actual shouldBe phoneNumber
  }

  private def checkNumberWithoutRegion(regionMetadata: RegionMetadata)(phoneNumber: String): Unit = {
    val parsed = PhoneNumberHelper.parseValidPhoneNumber(phoneNumber)
    regionMetadata.countryCodeToTypePatterns.keys should contain(parsed.getCountryCode)
    val actual = PhoneNumberHelper.Util.format(parsed, PhoneNumberFormat.E164)
    actual shouldBe phoneNumber
  }

  "Generators.ValidPhoneNumberGen" should {
    "returns valid phone numbers" in {
      forAll(Generators.ValidPhoneNumberGen)(checkPhoneNumber)
    }
  }

  "Generators.validPhoneNumberGen" should {
    "provide phone number generator" which {
      "returns valid phone numbers" when {
        Regions.All.foreach { region =>
          val regionMetadata = RegionMetadataProvider.forRegion(region)
          s"$region region was passed" in {
            val gen = Generators.validPhoneNumberGen(region)
            val checker = checkPhoneNumber(regionMetadata)(_)
            forAll(gen) { phoneNumber =>
              checker(phoneNumber)
            }
          }
          val phoneNumberTypes = regionMetadata.countryCodeToTypePatterns.values.flatMap(_.keys).toSet
          phoneNumberTypes.foreach { phoneNumberType =>
            s"$region region and $phoneNumberType phone number type were passed" in {
              val gen = Generators.validPhoneNumberGen(region)
              val checker = checkPhoneNumber(regionMetadata)(_)
              forAll(gen) { phoneNumber =>
                checker(phoneNumber)
              }
            }
          }
        }
      }
      "fails" when {
        Regions.All.foreach { region =>
          val regionMetadata = RegionMetadataProvider.forRegion(region)
          val phoneNumberTypes = regionMetadata.countryCodeToTypePatterns.values.flatMap(_.keys).toSet
          PhoneNumberTypes.All.diff(phoneNumberTypes).foreach { phoneNumberType =>
            s"unexpected $phoneNumberType phone number type passed for $region region" in {
              intercept[IllegalArgumentException] {
                Generators.validPhoneNumberGen(regionMetadata.region, phoneNumberType)
              }
            }
          }
        }
      }
    }
  }

}
