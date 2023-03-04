package io.github.mr_tolmach.generators

import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import io.github.mr_tolmach.metadata.RegionMetadataProvider
import io.github.mr_tolmach.metadata.model.PhoneNumberTypes.PhoneNumberType
import io.github.mr_tolmach.metadata.model.Regions.{NonGeo, Region}
import io.github.mr_tolmach.metadata.model.{PhoneNumberTypes, RegionMetadata, Regions}
import org.scalacheck.ShrinkLowPriority
import org.scalactic.anyvals.{PosInt, PosZDouble}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class E164GeneratorsSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with ShrinkLowPriority {

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

  private def checkPhoneNumber(regionMetadata: RegionMetadata, phoneNumberType: PhoneNumberType)(
      phoneNumber: String
  ): Unit = {
    checkNumberWithRegion(regionMetadata, phoneNumberType)(phoneNumber)
    checkNumberWithoutRegion(regionMetadata, phoneNumberType)(phoneNumber)
  }

  private def checkPhoneNumber(
      expectedRegion: Option[Region],
      expectedPhoneNumberType: Option[PhoneNumberType],
      possibleCountryCodes: Seq[Int]
  )(phoneNumber: String): Unit = {
    val parsed = expectedRegion match {
      case Some(region) => PhoneNumberHelper.parseValidPhoneNumber(phoneNumber, region)
      case None         => PhoneNumberHelper.parseValidPhoneNumber(phoneNumber)
    }
    val parsedPhoneNumber = PhoneNumberHelper.Util.format(parsed, PhoneNumberFormat.E164)
    parsedPhoneNumber shouldBe phoneNumber
    expectedRegion.foreach { expectedRegion =>
      val actualRegion = PhoneNumberHelper.Util.getRegionCodeForNumber(parsed) match {
        case "001" => NonGeo
        case name  => Regions.withName(name)
      }
      actualRegion shouldBe expectedRegion
    }
    expectedPhoneNumberType.foreach { expectedPhoneNumberType =>
      val normExpectedPhoneNumberType = expectedPhoneNumberType.toString.map(_.toLower)
      val normActualPhoneNumberType =
        PhoneNumberHelper.Util.getNumberType(parsed).toString.filter(_.isLetter).map(_.toLower)

      normActualPhoneNumberType shouldBe normExpectedPhoneNumberType
    }
    possibleCountryCodes should contain(parsed.getCountryCode)
  }

  private def checkNumberWithRegion(regionMetadata: RegionMetadata)(phoneNumber: String): Unit = {
    checkPhoneNumber(
      expectedRegion = Some(regionMetadata.region),
      expectedPhoneNumberType = None,
      possibleCountryCodes = regionMetadata.countryCodeToTypePatterns.keys.toSeq
    )(phoneNumber)
  }

  private def checkNumberWithRegion(regionMetadata: RegionMetadata, phoneNumberType: PhoneNumberType)(
      phoneNumber: String
  ): Unit = {
    checkPhoneNumber(
      expectedRegion = Some(regionMetadata.region),
      expectedPhoneNumberType = Some(phoneNumberType),
      possibleCountryCodes = regionMetadata.countryCodeToTypePatterns.keys.toSeq
    )(phoneNumber)
  }

  private def checkNumberWithoutRegion(regionMetadata: RegionMetadata)(phoneNumber: String): Unit = {
    checkPhoneNumber(
      expectedRegion = None,
      expectedPhoneNumberType = None,
      possibleCountryCodes = regionMetadata.countryCodeToTypePatterns.keys.toSeq
    )(phoneNumber)
  }

  private def checkNumberWithoutRegion(regionMetadata: RegionMetadata, phoneNumberType: PhoneNumberType)(
      phoneNumber: String
  ): Unit = {
    checkPhoneNumber(
      expectedRegion = None,
      expectedPhoneNumberType = Some(phoneNumberType),
      possibleCountryCodes = regionMetadata.countryCodeToTypePatterns.keys.toSeq
    )(phoneNumber)
  }

  "E164Generators.PhoneNumberGen" should {
    "returns valid phone numbers" in {
      forAll(E164Generators.PhoneNumberGen)(checkPhoneNumber)
    }
  }

  "E164Generators.phoneNumberGen" should {
    "provide phone number generator" which {
      "returns valid phone numbers" when {
        Regions.All.foreach { region =>
          val regionMetadata = RegionMetadataProvider.forRegion(region)
          s"$region region was passed" in {
            val gen = E164Generators.phoneNumberGen(region)
            val checker = checkPhoneNumber(regionMetadata)(_)
            forAll(gen) { phoneNumber =>
              checker(phoneNumber)
            }
          }
          val phoneNumberTypes = regionMetadata.countryCodeToTypePatterns.values.flatMap(_.keys).toSet
          phoneNumberTypes.foreach { phoneNumberType =>
            s"$region region and $phoneNumberType phone number type were passed" in {
              val gen = E164Generators.phoneNumberGen(region, phoneNumberType)
              val checker = checkPhoneNumber(regionMetadata, phoneNumberType)(_)
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
                E164Generators.phoneNumberGen(regionMetadata.region, phoneNumberType)
              }
            }
          }
        }
      }
    }
  }

}
