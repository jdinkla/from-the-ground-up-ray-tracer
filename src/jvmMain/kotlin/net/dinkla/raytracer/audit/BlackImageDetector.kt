package net.dinkla.raytracer.audit

import net.dinkla.raytracer.films.ColorGridFilm
import kotlin.math.max

/**
 * Measures how much of a rendered image is (near-)black — the signal the audit uses to flag scenes
 * that probably rendered nothing (a wrong material/light/camera, a broken tracer choice, an empty
 * frame). A pixel counts as near-black when its brightest channel is within [DEFAULT_EPSILON] of
 * zero, so true-black backgrounds and faint noise both register as black.
 */
object BlackImageDetector {
    const val DEFAULT_EPSILON = 1e-3

    fun nearBlackFraction(
        film: ColorGridFilm,
        epsilon: Double = DEFAULT_EPSILON,
    ): Double {
        val width = film.resolution.width
        val height = film.resolution.height
        val total = width * height
        if (total == 0) return 1.0
        var black = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val color = film.colorAt(x, y)
                if (max(color.red, max(color.green, color.blue)) <= epsilon) black++
            }
        }
        return black.toDouble() / total
    }
}
