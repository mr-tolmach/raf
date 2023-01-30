package io.github.mr_tolmach.metadata

import io.github.mr_tolmach.metadata.model.RegionMetadata
import io.github.mr_tolmach.metadata.model.Regions.Region
import org.xerial.snappy.Snappy

import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

object RegionMetadataProvider {

  private def metadataFileName(region: Region) = s"metadata/$region"

  private def readForRegion(region: Region): RegionMetadata = {
    val inputStream = this.getClass.getClassLoader.getResourceAsStream(metadataFileName(region))
    val bytes = Iterator.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray
    val decompressed = Snappy.uncompress(bytes)
    val line = new String(decompressed, StandardCharsets.UTF_8)
    RegionMetadata.fromString(line)
  }

  private val regionsMap = new ConcurrentHashMap[Region, RegionMetadata]()

  /** Returns region metadata for provided geographical and non-geographical region.
    *
    * @note
    *   use [[io.github.mr_tolmach.metadata.model.Regions.NonGeo Regions.NonGeo]] for non-geographical region
    *
    * @param region
    *   the region for which an metadata is needed
    * @return
    *   the metadata for provided region
    */
  def forRegion(region: Region): RegionMetadata = {
    Option(regionsMap.get(region)) match {
      case Some(metadata) =>
        metadata
      case None =>
        regionsMap.computeIfAbsent(region, _ => readForRegion(region))
    }
  }

}
