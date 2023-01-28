package io.github.mr_tolmach.metadata

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import io.github.mr_tolmach.metadata.model.RegionMetadata
import io.github.mr_tolmach.metadata.model.Regions.Region
import org.xerial.snappy.Snappy

import java.nio.charset.StandardCharsets

object RegionMetadataProvider {

  private val cache: LoadingCache[Region, RegionMetadata] = CacheBuilder.newBuilder().build {
    new CacheLoader[Region, RegionMetadata] {
      override def load(key: Region): RegionMetadata = {
        readForRegion(key)
      }
    }
  }

  private def readForRegion(region: Region): RegionMetadata = {
    val inputStream = this.getClass.getClassLoader.getResourceAsStream(metadataFileName(region))
    val bytes = LazyList.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray
    val decompressed = Snappy.uncompress(bytes)
    val line = new String(decompressed, StandardCharsets.UTF_8)
    RegionMetadata.fromString(line)
  }

  private def metadataFileName(region: Region) = s"metadata/$region"

  def forRegion(region: Region): RegionMetadata = {
    cache.get(region)
  }

}
