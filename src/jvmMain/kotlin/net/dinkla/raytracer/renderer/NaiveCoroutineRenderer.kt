package net.dinkla.raytracer.renderer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger

class NaiveCoroutineRenderer(
    private val render: ISingleRayRenderer,
    private val corrector: IColorCorrector
) : IRenderer {
    override fun render(film: IFilm) {
        Logger.info("render starts")
        runBlocking<Unit> {
            for (r in 0 until film.resolution.height) {
                for (c in 0 until film.resolution.width) {
                    launch(Dispatchers.Default) {
                        val color = corrector.correct(render.render(r, c)).clamp()
                        film.setPixel(c, r, color)
                    }
                }
            }
            Logger.info("runBlocking stops")
        }
        Logger.info("render stops")
    }
}
