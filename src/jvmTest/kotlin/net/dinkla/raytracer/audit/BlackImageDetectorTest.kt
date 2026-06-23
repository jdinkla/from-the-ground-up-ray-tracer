package net.dinkla.raytracer.audit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.ColorGridFilm
import net.dinkla.raytracer.utilities.Resolution

class BlackImageDetectorTest : StringSpec({
    "an unwritten film reads as fully black" {
        val film = ColorGridFilm(Resolution(4, 4))

        BlackImageDetector.nearBlackFraction(film) shouldBe 1.0
    }

    "an all-white film is not black at all" {
        val film = ColorGridFilm(Resolution(4, 4))
        fill(film, 4, 4, Color.WHITE)

        BlackImageDetector.nearBlackFraction(film) shouldBe 0.0
    }

    "a film lit in exactly half its rows is half black" {
        val film = ColorGridFilm(Resolution(4, 4)) // 16 pixels
        for (y in 2 until 4) {
            for (x in 0 until 4) film.setPixel(x, y, Color.WHITE)
        }

        BlackImageDetector.nearBlackFraction(film) shouldBe 0.5
    }

    "a pixel brighter than epsilon counts as lit" {
        val film = ColorGridFilm(Resolution(1, 1))
        film.setPixel(0, 0, Color(0.0, 0.0, 0.01)) // brightest channel 0.01 > default 1e-3

        BlackImageDetector.nearBlackFraction(film) shouldBe 0.0
    }

    "a pixel at or below epsilon counts as black" {
        val film = ColorGridFilm(Resolution(1, 1))
        film.setPixel(0, 0, Color(0.0, 0.0, 1e-4)) // below default epsilon

        BlackImageDetector.nearBlackFraction(film) shouldBe 1.0
    }
})

private fun fill(
    film: ColorGridFilm,
    width: Int,
    height: Int,
    color: Color,
) {
    for (y in 0 until height) {
        for (x in 0 until width) film.setPixel(x, y, color)
    }
}
