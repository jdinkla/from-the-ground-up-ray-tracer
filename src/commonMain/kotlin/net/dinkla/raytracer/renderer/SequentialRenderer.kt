package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger

class SequentialRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {
    override fun render(film: IFilm) {
        Logger.info("render starts")
        for (r in 0 until film.resolution.height) {
            for (c in 0 until film.resolution.width) {
                film.setPixel(c, r, render(r, c))
            }
        }
        Logger.info("render stops")
    }

    private fun render(r: Int, c: Int): Color = corrector.correct(render.render(r, c)).clamp()
}
