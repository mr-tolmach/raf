package io.github.mr_tolmach.metadata

import io.github.mr_tolmach.metadata.model.{RegionMetadata, Regions}
import io.github.mr_tolmach.metadata.model.Regions.Region
import org.xerial.snappy.Snappy
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object RegionMetadataProvider {

  lazy val All: Seq[RegionMetadata] = {
    Regions.All.toSeq.map(readForRegion)
  }

  private def metadataFileName(region: Region) = s"metadata/$region"

  private def readForRegion(region: Region): RegionMetadata = {
    val url = this.getClass.getClassLoader.getResource(metadataFileName(region))
    val path = Paths.get(url.toURI)
    val bytes = Files.readAllBytes(path)
    val decompressed = Snappy.uncompress(bytes)
    val line = new String(decompressed, StandardCharsets.UTF_8)
    RegionMetadata.fromString(line)
  }

  private lazy val MetadataMap: Map[Region, RegionMetadata] = All.map(m => m.region -> m).toMap

  def forRegion(region: Region): RegionMetadata = {
    MetadataMap(region)
  }

}
