package net.dinkla.raytracer.ui.swing

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicLong

class SwingFilm(
    override val resolution: Resolution,
) : IFilm {
    val image = BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_INT_RGB)

    /** Total pixels in the film; used by the UI to turn [renderedPixels] into a completion fraction. */
    val totalPixels: Long = resolution.width.toLong() * resolution.height.toLong()

    private val pixelsWritten = AtomicLong(0)

    /**
     * Number of pixels written so far. The parallel renderers call [setPixel] from many worker
     * threads, so this is an [AtomicLong] the EDT can read safely to drive a progress indicator
     * while the render is still running.
     */
    val renderedPixels: Long
        get() = pixelsWritten.get()

    override fun setPixel(
        x: Int,
        y: Int,
        color: Color,
    ) {
        image.setRGB(x, resolution.height - 1 - y, color.toInt())
        pixelsWritten.incrementAndGet()
    }
}
