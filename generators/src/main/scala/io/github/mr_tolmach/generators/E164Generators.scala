package io.github.mr_tolmach.generators

import io.github.mr_tolmach.metadata.RegionMetadataProvider
import io.github.mr_tolmach.metadata.model.PhoneNumberTypes.PhoneNumberType
import io.github.mr_tolmach.metadata.model.Regions.Region
import io.github.mr_tolmach.metadata.model.{RegionMetadata, Regions}
import org.scalacheck.Gen
import wolfendale.scalacheck.regexp.RegexpGen

/** This object provides a set of generators for generating valid phone numbers in the E.164 format.
  *
  * Basically, the E.164 format can be described by this regular expression: `^\+[1-9]\d{1,14}$`. But this regular
  * expression is only a basic template and may not describe all valid phone numbers in the E.164 format.
  *
  * The validity of the generated phone numbers is guaranteed by corrected regular expressions from
  * [[https://github.com/google/libphonenumber libphonenumber]] metadata. Regular expressions from libphonenumber cannot
  * be used as is, as there are conflicts in them.
  *
  * Generators presented in this object can be used with [[https://scalacheck.org/ scalacheck]] like this:
  * {{{
  *   forAll(E164Generators.PhoneNumberGen) { phoneNumber =>
  *       someChecks(phoneNumber)
  *   }
  * }}}
  *
  * @see
  *   [[io.github.mr_tolmach.metadata.model.RegionMetadata]]
  */
object E164Generators {

  /** A generator that returns random valid phone number in the E.164 format. */
  lazy val PhoneNumberGen: Gen[String] = for {
    region <- Gen.oneOf(Regions.All)
    phoneNumber <- phoneNumberGen(region)
  } yield phoneNumber

  /** Returns generator of valid phone numbers for the given region in the E.164 format.
    *
    * @param region
    *   the region for which generator is needed.
    * @return
    *   the generator of valid phone numbers for the given region in the E.164 format.
    */
  def phoneNumberGen(region: Region): Gen[String] = {
    val regionMetadata = RegionMetadataProvider.forRegion(region)
    phoneNumberGen(regionMetadata)
  }

  /** Returns generator of valid phone numbers for the given region and phone number type in the E.164 format.
    *
    * @param region
    *   the region for which generator is needed.
    * @param phoneNumberType
    *   the phone number type for which generator is needed.
    * @return
    *   the generator of valid phone numbers for the given region and phone number type in the E.164 format.
    * @throws IllegalArgumentException
    *   if a valid phone number does not exist for such a combination of region and phone number type.
    */
  def phoneNumberGen(region: Region, phoneNumberType: PhoneNumberType): Gen[String] = {
    val regionMetadata = RegionMetadataProvider.forRegion(region)
    phoneNumberGen(regionMetadata, phoneNumberType)
  }

  private def phoneNumberGen(regionMetadata: RegionMetadata): Gen[String] = {
    for {
      (countryCode, typeToPattern) <- Gen.oneOf(regionMetadata.countryCodeToTypePatterns)
      pattern <- Gen.oneOf(typeToPattern.values)
      nationalNumber <- RegexpGen.from(pattern)
    } yield toPhoneNumber(countryCode, nationalNumber)
  }

  private def phoneNumberGen(regionMetadata: RegionMetadata, phoneNumberType: PhoneNumberType): Gen[String] = {
    val countryCodeWithPattern = regionMetadata.countryCodeToTypePatterns.flatMap { case (countryCode, v) =>
      v.get(phoneNumberType).map { pattern => countryCode -> pattern }
    }.toSeq
    if (countryCodeWithPattern.nonEmpty) {
      for {
        (countryCode, pattern) <- Gen.oneOf(countryCodeWithPattern)
        nationalNumber <- RegexpGen.from(pattern)
      } yield toPhoneNumber(countryCode, nationalNumber)
    } else {
      throw new IllegalArgumentException(s"$phoneNumberType is not supported for ${regionMetadata.region} region")
    }
  }

  private def toPhoneNumber(countryCode: Int, nationalNumber: String): String = {
    s"+$countryCode$nationalNumber"
  }

}
