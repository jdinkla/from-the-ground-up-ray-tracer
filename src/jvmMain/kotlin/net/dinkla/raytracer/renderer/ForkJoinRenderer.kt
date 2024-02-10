package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction

class ForkJoinRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    private var sizeGrid: Int = 8
    private var film: IFilm? = null

    override fun render(film: IFilm) {
        this.film = film
        val master = Master(sizeGrid, film.resolution)
        Logger.info("invoke master")
        pool.invoke(master)
        this.film = null
    }

    internal inner class Master(private val numBlocks: Int, resolution: Resolution) : RecursiveAction() {
        private val blockHeight: Int = resolution.height / numBlocks
        private val blockWidth: Int = resolution.width / numBlocks
        private val blocks: Int = numBlocks * numBlocks
        private var actions: Array<Worker?> = arrayOfNulls(blocks)

        override fun compute() {
            Logger.info("Master.compute starts for $numBlocks * $numBlocks blocks")
            for (j in 0 until numBlocks) {
                for (i in 0 until numBlocks) {
                    val idx = j * numBlocks + i
                    val x = i * blockWidth
                    val y = j * blockHeight
                    val t = Worker(x, x + blockWidth, y, y + blockHeight)
                    actions[idx] = t
                    t.fork()
                }
            }
            for (i in 0 until blocks) {
                actions[i]?.join()
            }
            Logger.info("Master.compute ends")
        }
    }

    internal inner class Worker(
        private val xStart: Int, private val xEnd: Int, private val yStart: Int, private val yEnd: Int
    ) : RecursiveAction() {
        override fun compute() {
            var count = 0
            var r = yStart
            while (r < yEnd) {
                var c = xStart
                while (c < xEnd) {
                    val color = corrector.correct(render.render(r, c)).clamp()
                    film?.setPixel(c, r, color)
                    c += 1
                }
                count++
                r += 1
            }
        }
    }

    companion object {
        private var pool: ForkJoinPool = ForkJoinPool()
    }
}
