package net.dinkla.raytracer.renderer

import kotlinx.coroutines.*
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution

class CoroutineBlockRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    private var sizeGrid: Int = 32
    var exposureTime = 1.0

    override fun render(film: IFilm) {
        Logger.info("render starts")
        this.film = film
        runBlocking<Unit>(Dispatchers.Default) {
            Logger.info("invoke master")
            master(sizeGrid, film.resolution)
            Logger.info("runBlocking stops")
        }
        Logger.info("render stops")
        this.film = null
    }

    private var film: IFilm? = null

    suspend fun master(numBlocks: Int, resolution: Resolution) = coroutineScope {
        val blockHeight: Int = resolution.height / numBlocks
        val blockWidth: Int = resolution.width / numBlocks
        val blocks: Int = numBlocks * numBlocks
        val actions: Array<Job?> = arrayOfNulls(blocks)

        Logger.info("Master.compute starts for $numBlocks * $numBlocks blocks")
        for (j in 0 until numBlocks) {
            for (i in 0 until numBlocks) {
                val idx = j * numBlocks + i
                val x = i * blockWidth
                val y = j * blockHeight
                val job = launch(Dispatchers.Default) {
                    work(x, x + blockWidth, y, y + blockHeight)
                }
                actions[idx] = job
            }
        }
        for (i in 0 until blocks) {
            actions[i]?.join()
        }
        Logger.info("Master.compute ends")
    }

    suspend fun work(xStart: Int, xEnd: Int, yStart: Int, yEnd: Int) {
        var count = 0
        var r = yStart
        while (r < yEnd) {
            var c = xStart
            while (c < xEnd) {
                var color = render.render(r, c)
                color *= exposureTime
                color = corrector.correct(color)
                film?.setPixel(c, r, color.clamp())
                c += 1
                yield()
            }
            count++
            r += 1
        }
    }
}
