package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.mappings.Mapping

/**
 * A [Texture] backed by an in-memory raster [image]. When a [mapping] is supplied the texture asks
 * it where the (local) hit point lands in the image; otherwise it uses the shading record's
 * parametric texture coordinates [IShade.u]/[IShade.v] directly (the path objects that compute their
 * own UVs would use). The pixel lookup itself is pure and lives here in `commonMain`; only decoding a
 * file into an [Image] is JVM I/O (see `jvmMain`'s `ImageReader`).
 *
 * Mirrors Suffern's `ImageTexture::get_color` (Ray Tracing from the Ground Up, ch. 29).
 */
class ImageTexture(
    val image: Image,
    val mapping: Mapping? = null,
) : Texture {
    override fun getColor(sr: IShade): Color {
        val texel =
            mapping?.getTexelCoordinates(sr.localHitPoint, image.hres, image.vres)
        return if (texel != null) {
            image.getColor(texel.row, texel.column)
        } else {
            // No mapping: read parametric UVs from the hit record (row from the top).
            val column = (sr.u * (image.hres - 1)).toInt()
            val row = ((1.0 - sr.v) * (image.vres - 1)).toInt()
            image.getColor(row, column)
        }
    }
}
