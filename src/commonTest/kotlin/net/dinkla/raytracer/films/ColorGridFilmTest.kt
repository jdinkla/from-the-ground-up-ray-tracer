package net.dinkla.raytracer.films

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

class ColorGridFilmTest :
    StringSpec({
        "reads back the colour written to a pixel" {
            val film = ColorGridFilm(Resolution(3, 2))

            film.setPixel(2, 1, Color(0.1, 0.2, 0.3))

            film.colorAt(2, 1) shouldBe Color(0.1, 0.2, 0.3)
        }

        "an unwritten pixel reads as black" {
            val film = ColorGridFilm(Resolution(3, 2))

            film.colorAt(0, 0) shouldBe Color.BLACK
        }

        "writing one pixel does not affect its neighbours" {
            val film = ColorGridFilm(Resolution(3, 2))

            film.setPixel(1, 0, Color.RED)

            film.colorAt(0, 0) shouldBe Color.BLACK
            film.colorAt(2, 0) shouldBe Color.BLACK
            film.colorAt(1, 1) shouldBe Color.BLACK
        }
    })
