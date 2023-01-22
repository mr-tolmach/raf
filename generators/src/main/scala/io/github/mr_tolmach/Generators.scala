package io.github.mr_tolmach

import io.github.mr_tolmach.metadata.RegionMetadataProvider
import io.github.mr_tolmach.metadata.model.PhoneNumberTypes.PhoneNumberType
import io.github.mr_tolmach.metadata.model.RegionMetadata
import io.github.mr_tolmach.metadata.model.Regions.Region
import org.scalacheck.Gen
import wolfendale.scalacheck.regexp.RegexpGen

object Generators {

  val ValidPhoneNumberGen: Gen[String] = for {
    regionMetadata <- Gen.oneOf(RegionMetadataProvider.All)
    phoneNumber <- validPhoneNumberGen(regionMetadata)
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
      pattern <- Gen.oneOf(regionMetadata.typeToPattern.values)
      nationalPart <- RegexpGen.from(pattern)
    } yield toPhoneNumber(regionMetadata.countryCode, nationalPart)
  }

  private def validPhoneNumberGen(regionMetadata: RegionMetadata, phoneNumberType: PhoneNumberType): Gen[String] = {
    regionMetadata.typeToPattern.get(phoneNumberType) match {
      case Some(pattern) =>
        RegexpGen.from(pattern).map { nationalPart =>
          toPhoneNumber(regionMetadata.countryCode, nationalPart)
        }
      case None =>
        throw new IllegalArgumentException(s"$phoneNumberType is not supported for ${regionMetadata.region} region")
    }
  }

  private def toPhoneNumber(countryCode: Int, nationalPart: String): String = {
    s"+$countryCode$nationalPart"
  }

}
