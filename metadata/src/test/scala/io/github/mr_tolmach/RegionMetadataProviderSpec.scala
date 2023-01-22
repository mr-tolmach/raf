package io.github.mr_tolmach

import io.github.mr_tolmach.metadata.RegionMetadataProvider
import io.github.mr_tolmach.metadata.model.Regions
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class RegionMetadataProviderSpec extends AnyWordSpec {

  "RegionMetadataProvider" should {
    "return metadata" when {
      Regions.All.foreach { region =>
        s"$region region passed" in {
          assert(Try(RegionMetadataProvider.forRegion(region)).isSuccess)
        }
      }
    }
  }

}
