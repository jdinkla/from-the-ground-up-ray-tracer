package net.dinkla.raytracer.renderer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.utilities.Logger

class CoroutineRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    private var exposureTime = 1.0

    override fun render(film: Film) {
        Logger.info("render starts")
        runBlocking<Unit> {
            for (r in 0 until film.resolution.width) {
                for (c in 0 until film.resolution.height) {
                    launch(pool) {
                        var color = render.render(r, c)
                        color *= exposureTime
                        color = corrector.correct(color)
                        film.setPixel(c, r, color.clamp())
                    }
                }
            }
            Logger.info("runBlocking stops")
        }
        Logger.info("render stops")
    }

    companion object {
        internal val pool = Dispatchers.Default
    }

}
