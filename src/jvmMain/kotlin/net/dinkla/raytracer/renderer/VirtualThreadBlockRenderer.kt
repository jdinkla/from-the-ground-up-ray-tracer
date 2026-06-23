package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import java.util.concurrent.atomic.AtomicReference

private const val NUMBER_OF_BLOCKS = 32

class VirtualThreadBlockRenderer(
    private val render: ISingleRayRenderer,
    private val corrector: IColorCorrector,
) : IRenderer {
    private var film: IFilm? = null

    override fun render(
        film: IFilm,
        cancellation: CancellationToken,
    ) {
        Logger.info("render starts")
        this.film = film
        Logger.info("invoke master")
        master(NUMBER_OF_BLOCKS, film.resolution, cancellation)
        Logger.info("render stops")
        this.film = null
    }

    private fun master(
        numBlocks: Int,
        resolution: Resolution,
        cancellation: CancellationToken,
    ) {
        val factory = Thread.ofVirtual().factory()
        Logger.info("Master.compute starts for $numBlocks * $numBlocks blocks")
        // Thread.join() does NOT rethrow an exception thrown inside the thread body, so a worker
        // failure (e.g. an incompatible tracer raising UnsupportedOperationException) would otherwise
        // be swallowed and render(film) would return a half-filled film. Capture the first failure and
        // rethrow it after joining so the failure surfaces to the caller (TASK-45).
        val failure = AtomicReference<Throwable?>(null)
        Block
            .partitionIntoBlocks(numBlocks, resolution)
            .map {
                val virtualThread = factory.newThread { work(it, cancellation, failure) }
                virtualThread.start()
                virtualThread
            }.map { it.join() }
        Logger.info("Master.compute ends")
        failure.get()?.let { throw it }
    }

    // Broad catch is deliberate: a pixel shade can fail in many unrelated ways. We record the first
    // failure (first write wins) so master() can rethrow it; without this Thread.join would swallow it.
    @Suppress("TooGenericExceptionCaught")
    private fun work(
        block: Block,
        cancellation: CancellationToken,
        failure: AtomicReference<Throwable?>,
    ) {
        try {
            for (y in block.yStart until block.yEnd) {
                // Poll once per row so an in-flight block stops promptly on cancellation, and unstarted
                // blocks skip their work entirely.
                if (cancellation.isCancelled) {
                    return
                }
                for (x in block.xStart until block.xEnd) {
                    val color = corrector.correct(render.render(y, x)).clamp()
                    film?.setPixel(x, y, color)
                }
            }
        } catch (e: Throwable) {
            failure.compareAndSet(null, e)
        }
    }
}
