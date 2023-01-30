package io.github.mr_tolmach.metadata

import io.github.mr_tolmach.metadata.model.RegionMetadata
import io.github.mr_tolmach.metadata.model.Regions.Region
import org.xerial.snappy.Snappy

import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

object RegionMetadataProvider {

  private def readForRegion(region: Region): RegionMetadata = {
    val inputStream = this.getClass.getClassLoader.getResourceAsStream(metadataFileName(region))
    val bytes = Stream.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray
    val decompressed = Snappy.uncompress(bytes)
    val line = new String(decompressed, StandardCharsets.UTF_8)
    RegionMetadata.fromString(line)
  }

  private def metadataFileName(region: Region) = s"metadata/$region"

  private val regionsMap = new ConcurrentHashMap[Region, RegionMetadata]()

  def forRegion(region: Region): RegionMetadata = {
    Option(regionsMap.get(region)) match {
      case Some(metadata) =>
        metadata
      case None =>
        regionsMap.computeIfAbsent(region, _ => readForRegion(region))
    }
  }

}
