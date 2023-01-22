package io.github.t0lmach

import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import io.github.t0lmach.metadata.RegionMetadataProvider
import io.github.t0lmach.metadata.model.{PhoneNumberTypes, RegionMetadata}
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
    parsed.getCountryCode shouldBe regionMetadata.countryCode
    val actual = PhoneNumberHelper.Util.format(parsed, PhoneNumberFormat.E164)
    actual shouldBe phoneNumber
  }

  private def checkNumberWithoutRegion(regionMetadata: RegionMetadata)(phoneNumber: String): Unit = {
    val parsed = PhoneNumberHelper.parseValidPhoneNumber(phoneNumber)
    parsed.getCountryCode shouldBe regionMetadata.countryCode
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
        RegionMetadataProvider.All.foreach { regionMetadata =>
          s"${regionMetadata.region} region was passed" in {
            val gen = Generators.validPhoneNumberGen(regionMetadata.region)
            val checker = checkPhoneNumber(regionMetadata)(_)
            forAll(gen) { phoneNumber =>
              checker(phoneNumber)
            }
          }
          regionMetadata.typeToPattern.keys.foreach { phoneNumberType =>
            s"${regionMetadata.region} region and $phoneNumberType phone number type were passed" in {
              val gen = Generators.validPhoneNumberGen(regionMetadata.region, phoneNumberType)
              val checker = checkPhoneNumber(regionMetadata)(_)
              forAll(gen) { phoneNumber =>
                checker(phoneNumber)
              }
            }
          }
        }
      }
      "fails" when {
        RegionMetadataProvider.All.foreach { regionMetadata =>
          PhoneNumberTypes.All.diff(regionMetadata.typeToPattern.keys.toSet).foreach { phoneNumberType =>
            s"unexpected $phoneNumberType phone number type passed for ${regionMetadata.region} region" in {
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
