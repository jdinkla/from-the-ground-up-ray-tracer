package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.utilities.Logger
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction

class ForkJoinRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {

    private var sizeGrid: Int = 4
    var exposureTime = 1.0
    private var film: Film? = null

    override fun render(film: Film) {
        this.film = film
        val res = film.resolution
        val master = Master(sizeGrid, res.hres, res.vres)
        Logger.info("invoke master")
        pool.invoke(master)
        this.film = null
    }

    internal inner class Master(private val numBlocks: Int, width: Int, height: Int) : RecursiveAction() {
        private val blockHeight: Int = height / numBlocks
        private val blockWidth: Int = width / numBlocks
        private val blocks: Int = numBlocks * numBlocks
        private var actions: Array<Worker?> = arrayOfNulls(blocks)

        override fun compute() {
            Logger.info("Master.compute starts")
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

    internal inner class Worker(private val xStart: Int, private val xEnd: Int, private val yStart: Int, private val yEnd: Int) : RecursiveAction() {
        override fun compute() {
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
