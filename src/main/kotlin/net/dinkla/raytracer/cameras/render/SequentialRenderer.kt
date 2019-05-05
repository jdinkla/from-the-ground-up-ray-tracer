package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.Film
import org.slf4j.LoggerFactory
import java.util.concurrent.ForkJoinPool

class SequentialRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    var exposureTime = 1.0

    override fun render(film: Film) {
        LOGGER.info("render starts")
        for (r in 0 until film.resolution.vres) {
            for (c in 0 until film.resolution.hres) {
                var color = render.render(r, c)
                color *= exposureTime
                color = corrector.correct(color)
                film.setPixel(c, r, color.clamp())
            }
        }
        LOGGER.info("render stops")
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(this::class.java)
    }

}
