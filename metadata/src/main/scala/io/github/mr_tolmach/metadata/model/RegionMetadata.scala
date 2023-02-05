package io.github.mr_tolmach.metadata.model

import PhoneNumberTypes.PhoneNumberType
import Regions.Region

/** This class stores a metadata for region.
  *
  * The metadata consists of mapping of phone number type to regex for each country code represented in that region.
  *
  * Each regular expression in combination with the associated country code can be used to generate valid phone numbers.
  * Generated phone numbers can be parsed by [[https://github.com/google/libphonenumber libphonenumber]] '''BUT''' make
  * sure you are using a comparable version of this library, otherwise, valid phone numbers may be recognized as
  * invalid.
  *
  * @param region
  *   the region for which metadata is provided.
  * @param countryCodeToTypePatterns
  *   the mapping of phone number type to regex pattern for each country code represented in that region.
  */
case class RegionMetadata(region: Region, countryCodeToTypePatterns: Map[Int, Map[PhoneNumberType, String]])

object RegionMetadata {

  /** Separator used to separate the two parts of the input string. */
  private val MainPartSeparator = '#'

  /** Separator used to separate the country code and phone number type pattern information. */
  private val CountryCodeToTypePatternsSeparator = ':'

  /** Returns a [[RegionMetadata]] for a string line in the expected format.
    *
    * Expected format:
    *   - The string input is expected to have two parts separated by the '#' character, with the first part being the
    *     region name and the second part being the country code and phone number type pattern information.
    *   - The country code and phone number type pattern information is expected to have multiple elements separated by
    *     the ':' character, where each element contains a country code and its associated phone number type pattern.
    *   - The phone number type pattern information is expected to be in pairs of type id and pattern, separated by the
    *     ':' character.
    *
    * @param line
    *   the input string
    * @return
    *   the [[RegionMetadata]] instance
    * @throws IllegalArgumentException
    *   if the input string does not match the expected format
    */
  def fromString(line: String): RegionMetadata = {
    val parts = line.split(MainPartSeparator)
    parts match {
      case Array(regionName, tail @ _*) =>
        val region = Regions.withName(regionName)
        val countryCodeToTypePatterns = tail
          .map(_.split(CountryCodeToTypePatternsSeparator))
          .map { case Array(countyCode, tail @ _*) =>
            val typeToPattern = tail
              .grouped(2)
              .map {
                case Seq(typeId, pattern) =>
                  val tpe = PhoneNumberTypes(typeId.toInt)
                  tpe -> pattern
                case _ =>
                  throw new IllegalArgumentException(s"Unexpected input: $line")
              }
              .toMap
            countyCode.toInt -> typeToPattern
          }
          .toMap

        RegionMetadata(
          region,
          countryCodeToTypePatterns
        )
      case _ =>
        throw new IllegalArgumentException(s"Unexpected input: $line")
    }
  }

}
