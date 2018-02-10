package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm

class SequentialRenderer(protected val render: ISingleRayRenderer, protected val corrector: IColorCorrector) : IRenderer {

    var exposureTime = 1.0

    override fun render(film: IFilm, frame: Int) {
        for (r in 0 until film.resolution.vres) {
            for (c in 0 until film.resolution.hres) {
                var color = render.render(r, c)
                color = color.mult(exposureTime)
                color = corrector.correct(color)
                film.setPixel(frame, c, r, color)
            }
        }
    }

}
