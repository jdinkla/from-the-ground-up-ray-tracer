package net.dinkla.raytracer.renderer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution

private const val NUMBER_OF_BLOCKS = 32

class CoroutineBlockRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) :
    IRenderer {

    private var film: IFilm? = null

    override fun render(film: IFilm) {
        Logger.info("render starts")
        this.film = film
        runBlocking(Dispatchers.Default) {
            Logger.info("invoke master")
            master(NUMBER_OF_BLOCKS, film.resolution)
            Logger.info("runBlocking stops")
        }
        Logger.info("render stops")
        this.film = null
    }


    private suspend fun master(numBlocks: Int, resolution: Resolution) = coroutineScope {
        val blockHeight: Int = resolution.height / numBlocks
        val blockWidth: Int = resolution.width / numBlocks

        Logger.info("Master.compute starts for $numBlocks * $numBlocks blocks")
        val jobs = buildList() {
            for (j in 0 until numBlocks) {
                for (i in 0 until numBlocks) {
                    val x = i * blockWidth
                    val y = j * blockHeight
                    val job = launch(Dispatchers.Default) {
                        work(x, x + blockWidth, y, y + blockHeight)
                    }
                    add(job)
                }
            }
        }
        jobs.joinAll()
        Logger.info("Master.compute ends")
    }

    private suspend fun work(xStart: Int, xEnd: Int, yStart: Int, yEnd: Int) {
        for (y in yStart until yEnd) {
            for (x in xStart until xEnd) {
                val color = corrector.correct(render.render(y, x)).clamp()
                film?.setPixel(x, y, color)
            }
            yield()
        }
    }
}
