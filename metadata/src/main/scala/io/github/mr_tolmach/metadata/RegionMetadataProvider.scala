package io.github.mr_tolmach.metadata

import io.github.mr_tolmach.metadata.model.RegionMetadata
import io.github.mr_tolmach.metadata.model.Regions.Region
import org.xerial.snappy.Snappy

import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

/** This object provides metadata for geographical and non-geographical regions
  */
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

  /** Returns [[RegionMetadata]] for the given geographical and non-geographical region.
    *
    * Metadata for each region will be loaded only once.
    *
    * Also, this method is thread-safe and can be safely called from multiple threads concurrently. In this case, the
    * guarantee of loading metadata for each region only once remains unchanged.
    *
    * @param region
    *   the region for which [[RegionMetadata]] is needed
    * @return
    *   the [[RegionMetadata]] for the given region
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
