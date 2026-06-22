package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color

/**
 * An in-memory raster of colours, indexed by `(row, column)` with the row measured from the top.
 * This is the platform-independent image holder an [ImageTexture] samples; JVM file decoding lives
 * in `jvmMain` (`ImageReader`) and produces one of these, keeping the sampling math testable in
 * `commonMain` without any I/O.
 *
 * Mirrors the role of Suffern's `Image` (Ray Tracing from the Ground Up, ch. 29).
 */
class Image(
    val hres: Int,
    val vres: Int,
    private val pixels: Array<Color>,
) {
    init {
        require(hres > 0 && vres > 0) { "Image dimensions must be positive, got ${hres}x$vres" }
        require(pixels.size == hres * vres) {
            "Expected ${hres * vres} pixels for a ${hres}x$vres image, got ${pixels.size}"
        }
    }

    /**
     * The colour at `(row, column)`. Out-of-range coordinates are clamped to the nearest edge pixel
     * so a mapping that lands just outside the image returns a sensible colour instead of failing.
     */
    fun getColor(
        row: Int,
        column: Int,
    ): Color {
        val r = row.coerceIn(0, vres - 1)
        val c = column.coerceIn(0, hres - 1)
        return pixels[r * hres + c]
    }

    companion object {
        /**
         * Builds an [Image] from a [colorAt] function evaluated at every `(row, column)`. Handy for
         * tests and procedurally generated rasters.
         */
        fun of(
            hres: Int,
            vres: Int,
            colorAt: (row: Int, column: Int) -> Color,
        ): Image {
            val pixels = Array(hres * vres) { index -> colorAt(index / hres, index % hres) }
            return Image(hres, vres, pixels)
        }
    }
}
