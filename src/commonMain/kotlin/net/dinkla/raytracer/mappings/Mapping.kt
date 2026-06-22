package net.dinkla.raytracer.mappings

import net.dinkla.raytracer.math.Point3D

/**
 * A mapping converts a (local) hit point on a surface into integer pixel coordinates of an image,
 * given the image resolution. It is the bridge between geometry and an [net.dinkla.raytracer
 * .textures.ImageTexture]: the texture asks the mapping where in the image a hit point lands.
 *
 * The returned [Texel] is `(row, column)` with the row measured from the top of the image, matching
 * Suffern's `Mapping::get_texel_coordinates` (Ray Tracing from the Ground Up, ch. 29). Both
 * components are clamped to valid ranges by the texture before use.
 */
interface Mapping {
    /**
     * Maps [localHitPoint] to a texel of an image that is [hres] pixels wide and [vres] pixels tall.
     */
    fun getTexelCoordinates(
        localHitPoint: Point3D,
        hres: Int,
        vres: Int,
    ): Texel
}

/** A pixel address in an image: [row] from the top, [column] from the left (both 0-based). */
data class Texel(
    val row: Int,
    val column: Int,
)
