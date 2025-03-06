package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction

private const val NUMBER_OF_BLOCKS = 8

class ForkJoinRenderer(
    private val render: ISingleRayRenderer,
    private val corrector: IColorCorrector,
) : IRenderer {
    private var film: IFilm? = null

    override fun render(film: IFilm) {
        this.film = film
        val master = Master(NUMBER_OF_BLOCKS, film.resolution)
        Logger.info("invoke master")
        pool.invoke(master)
        this.film = null
    }

    internal inner class Master(
        private val numBlocks: Int,
        val resolution: Resolution,
    ) : RecursiveAction() {
        override fun compute() {
            Logger.info("Master.compute starts for $numBlocks * $numBlocks blocks")
            Block
                .partitionIntoBlocks(numBlocks, resolution)
                .map { Worker(it).fork() }
                .map { it.join() }
            Logger.info("Master.compute ends")
        }
    }

    internal inner class Worker(
        private val block: Block,
    ) : RecursiveAction() {
        override fun compute() {
            for (y in block.yStart until block.yEnd) {
                for (x in block.xStart until block.xEnd) {
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
