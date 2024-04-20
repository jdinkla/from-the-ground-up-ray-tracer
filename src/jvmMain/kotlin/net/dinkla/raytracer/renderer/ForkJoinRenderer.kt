package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction

private const val NUMBER_OF_BLOCKS = 8

class ForkJoinRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    private var film: IFilm? = null

    override fun render(film: IFilm) {
        this.film = film
        val master = Master(NUMBER_OF_BLOCKS, film.resolution)
        Logger.info("invoke master")
        pool.invoke(master)
        this.film = null
    }

    internal inner class Master(private val numBlocks: Int, resolution: Resolution) : RecursiveAction() {
        private val blockHeight: Int = resolution.height / numBlocks
        private val blockWidth: Int = resolution.width / numBlocks
        private val blocks: Int = numBlocks * numBlocks
        private var workers: Array<Worker?> = arrayOfNulls(blocks)

        override fun compute() {
            Logger.info("Master.compute starts for $numBlocks * $numBlocks blocks")
            for (j in 0 until numBlocks) {
                for (i in 0 until numBlocks) {
                    val x = i * blockWidth
                    val y = j * blockHeight
                    val t = Worker(x, x + blockWidth, y, y + blockHeight)
                    workers[j * numBlocks + i] = t
                    t.fork()
                }
            }
            for (i in 0 until blocks) {
                workers[i]?.join()
            }
            Logger.info("Master.compute ends")
        }
    }

    internal inner class Worker(
        private val xStart: Int, private val xEnd: Int, private val yStart: Int, private val yEnd: Int
    ) : RecursiveAction() {
        override fun compute() {
            for (y in yStart until yEnd) {
                for (x in xStart until xEnd) {
                    val color = corrector.correct(render.render(y, x)).clamp()
                    film?.setPixel(x, y, color)
                }
            }
        }
    }

    companion object {
        private var pool: ForkJoinPool = ForkJoinPool()
    }
}
