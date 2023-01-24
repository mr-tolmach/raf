package io.github.mr_tolmach.generators

import io.github.mr_tolmach.metadata.RegionMetadataProvider
import io.github.mr_tolmach.metadata.model.PhoneNumberTypes.PhoneNumberType
import io.github.mr_tolmach.metadata.model.Regions.Region
import io.github.mr_tolmach.metadata.model.{RegionMetadata, Regions}
import org.scalacheck.Gen
import wolfendale.scalacheck.regexp.RegexpGen

object Generators {

  val ValidPhoneNumberGen: Gen[String] = for {
    region <- Gen.oneOf(Regions.All)
    phoneNumber <- validPhoneNumberGen(region)
  } yield phoneNumber

  def validPhoneNumberGen(region: Region): Gen[String] = {
    val regionMetadata = RegionMetadataProvider.forRegion(region)
    validPhoneNumberGen(regionMetadata)
  }

  def validPhoneNumberGen(region: Region, phoneNumberType: PhoneNumberType): Gen[String] = {
    val regionMetadata = RegionMetadataProvider.forRegion(region)
    validPhoneNumberGen(regionMetadata, phoneNumberType)
  }

  private def validPhoneNumberGen(regionMetadata: RegionMetadata): Gen[String] = {
    for {
      (countryCode, typeToPattern) <- Gen.oneOf(regionMetadata.countryCodeToTypePatterns)
      pattern <- Gen.oneOf(typeToPattern.values)
      nationalPart <- RegexpGen.from(pattern)
    } yield toPhoneNumber(countryCode, nationalPart)
  }

  private def validPhoneNumberGen(regionMetadata: RegionMetadata, phoneNumberType: PhoneNumberType): Gen[String] = {
    val countryCodeWithPattern = regionMetadata.countryCodeToTypePatterns.flatMap { case (countryCode, v) =>
      v.get(phoneNumberType).map { pattern => countryCode -> pattern}
    }.toSeq
    if (countryCodeWithPattern.nonEmpty) {
      for {
        (countryCode, pattern) <- Gen.oneOf(countryCodeWithPattern)
        nationalPart <- RegexpGen.from(pattern)
      } yield toPhoneNumber(countryCode, nationalPart)
    } else {
      throw new IllegalArgumentException(s"$phoneNumberType is not supported for ${regionMetadata.region} region")
    }
  }

  private def toPhoneNumber(countryCode: Int, nationalPart: String): String = {
    s"+$countryCode$nationalPart"
  }

}
