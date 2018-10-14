package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm

class SequentialRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    var exposureTime = 1.0

    override fun render(film: IFilm) {
        for (r in 0 until film.resolution.vres) {
            for (c in 0 until film.resolution.hres) {
                var color = render.render(r, c)
                color = color.times(exposureTime)
                color = corrector.correct(color)
                film.setPixel(c, r, color)
            }
        }
    }

}
