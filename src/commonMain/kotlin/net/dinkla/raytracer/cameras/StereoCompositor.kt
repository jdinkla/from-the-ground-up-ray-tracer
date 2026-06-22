package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm

/**
 * Composites two single-eye images (a `width × height` grid each, read via [PixelSource]) into one
 * output [IFilm], according to [StereoViewing]. Pure pixel math — no rendering — so it is unit-tested
 * against tiny fake pixel sources.
 *
 * - [sideBySide] writes a `2·width × height` image: the left eye fills columns `[0, width)` and the
 *   right eye fills columns `[width, 2·width)`.
 * - [anaglyph] writes a `width × height` image whose red channel comes from the left eye and whose
 *   green/blue channels come from the right eye (the red/cyan anaglyph convention).
 */
object StereoCompositor {
    /** A read-only source of pixel colours for a single eye image. */
    fun interface PixelSource {
        fun colorAt(
            x: Int,
            y: Int,
        ): Color
    }

    /**
     * Writes the side-by-side composite of [left] and [right] (each [width]×[height]) into [out],
     * which must be `2·width` wide and [height] tall.
     */
    fun sideBySide(
        left: PixelSource,
        right: PixelSource,
        width: Int,
        height: Int,
        out: IFilm,
    ) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                out.setPixel(x, y, left.colorAt(x, y))
                out.setPixel(x + width, y, right.colorAt(x, y))
            }
        }
    }

    /**
     * Writes the anaglyph composite of [left] and [right] (each [width]×[height]) into [out], which
     * must be [width]×[height]: each output pixel takes its red channel from [left] and its green and
     * blue channels from [right].
     */
    fun anaglyph(
        left: PixelSource,
        right: PixelSource,
        width: Int,
        height: Int,
        out: IFilm,
    ) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                val l = left.colorAt(x, y)
                val r = right.colorAt(x, y)
                out.setPixel(x, y, Color(l.red, r.green, r.blue))
            }
        }
    }
}
