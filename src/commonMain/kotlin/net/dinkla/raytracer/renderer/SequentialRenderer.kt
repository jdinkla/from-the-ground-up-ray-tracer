package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.utilities.Logger

class SequentialRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    var exposureTime = 1.0

    override fun render(film: Film) {
        Logger.info("render starts")
        for (r in 0 until film.resolution.width) {
            for (c in 0 until film.resolution.height) {
                film.setPixel(c, r, render(r, c))
            }
        }
        Logger.info("render stops")
    }

    private fun render(c: Int, r: Int): Color {
        var color = render.render(c, r)
        color *= exposureTime
        color = corrector.correct(color)
        return color.clamp()
    }
}
