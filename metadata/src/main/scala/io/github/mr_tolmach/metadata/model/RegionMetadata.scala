package io.github.mr_tolmach.metadata.model

import PhoneNumberTypes.PhoneNumberType
import Regions.Region

/**
 *
 * @param region
 * @param countryCodeToTypePatterns
 */
case class RegionMetadata(region: Region, countryCodeToTypePatterns: Map[Int, Map[PhoneNumberType, String]])

object RegionMetadata {

  private val MainPartSeparator = '#'
  private val CountryCodeToTypePatternsSeparator = ':'

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
