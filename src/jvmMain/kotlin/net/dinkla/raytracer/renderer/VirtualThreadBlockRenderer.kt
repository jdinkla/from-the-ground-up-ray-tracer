package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution

private const val NUMBER_OF_BLOCKS = 32

class VirtualThreadBlockRenderer(
    private val render: ISingleRayRenderer,
    private val corrector: IColorCorrector,
) : IRenderer {
    private var film: IFilm? = null

    override fun render(film: IFilm) {
        Logger.info("render starts")
        this.film = film
        Logger.info("invoke master")
        master(NUMBER_OF_BLOCKS, film.resolution)
        Logger.info("render stops")
        this.film = null
    }

    private fun master(
        numBlocks: Int,
        resolution: Resolution,
    ) {
        val factory = Thread.ofVirtual().factory()
        Logger.info("Master.compute starts for $numBlocks * $numBlocks blocks")
        Block
            .partitionIntoBlocks(numBlocks, resolution)
            .map {
                val virtualThread = factory.newThread { work(it) }
                virtualThread.start()
                virtualThread
            }.map { it.join() }
        Logger.info("Master.compute ends")
    }

    private fun work(block: Block) {
        for (y in block.yStart until block.yEnd) {
            for (x in block.xStart until block.xEnd) {
                val color = corrector.correct(render.render(y, x)).clamp()
                film?.setPixel(x, y, color)
            }
        }
    }
}
