package net.dinkla.raytracer.cameras.render

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.interfaces.jvm.getLogger

class CoroutineRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    private var exposureTime = 1.0

    override fun render(film: Film) {
        LOGGER.info("render starts")
        runBlocking<Unit> {
            for (r in 0 until film.resolution.vres) {
                for (c in 0 until film.resolution.hres) {
                    launch(pool) {
                        var color = render.render(r, c)
                        color *= exposureTime
                        color = corrector.correct(color)
                        film.setPixel(c, r, color.clamp())
                    }
                }
            }
            LOGGER.info("runBlocking stops")
        }
        LOGGER.info("render stops")
    }

    companion object {
        internal val pool = Dispatchers.Default
        internal val LOGGER = getLogger(this::class.java)
    }

}
