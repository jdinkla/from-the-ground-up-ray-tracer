package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

/**
 * An in-memory [IFilm] that retains the pixels written to it so they can be read back. Used to render
 * a single stereo eye view into a buffer that is then read by
 * [StereoCompositor][net.dinkla.raytracer.cameras.StereoCompositor] to build the composite image.
 *
 * Unwritten pixels read as [Color.BLACK].
 */
class ColorGridFilm(
    override val resolution: Resolution,
) : IFilm {
    private val pixels = Array(resolution.width * resolution.height) { Color.BLACK }

    override fun setPixel(
        x: Int,
        y: Int,
        color: Color,
    ) {
        pixels[index(x, y)] = color
    }

    /** The colour previously written to pixel ([x], [y]), or [Color.BLACK] if none was written. */
    fun colorAt(
        x: Int,
        y: Int,
    ): Color = pixels[index(x, y)]

    private fun index(
        x: Int,
        y: Int,
    ): Int = y * resolution.width + x
}
