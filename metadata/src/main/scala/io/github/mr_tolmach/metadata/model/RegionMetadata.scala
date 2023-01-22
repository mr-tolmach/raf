package io.github.mr_tolmach.metadata.model

import PhoneNumberTypes.PhoneNumberType
import Regions.Region

case class RegionMetadata(region: Region,
                          countryCode: Int,
                          typeToPattern: Map[PhoneNumberType, String])

object RegionMetadata {

  private val Separator = ':'

  def fromString(line: String): RegionMetadata = {
    val parts = line.split(Separator)
    parts match {
      case Array(regionName, countryCode, tail@_*) =>
        val region = Regions.withName(regionName)
        val typeToPattern = tail.grouped(2).map {
          case Seq(typeId, pattern) =>
            val tpe = PhoneNumberTypes(typeId.toInt)
            tpe -> pattern
          case _ =>
            throw new IllegalArgumentException(s"Unexpected input: $line")
        }.toMap

        RegionMetadata(
          region,
          countryCode.toInt,
          typeToPattern
        )
      case _ =>
        throw new IllegalArgumentException(s"Unexpected input: $line")
    }
  }

}
