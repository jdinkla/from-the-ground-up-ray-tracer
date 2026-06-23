package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CyclicBarrier

class ParallelRenderer(
    private val render: ISingleRayRenderer,
    private val corrector: IColorCorrector,
) : IRenderer {
    private var parallel = false
    private var numThreads: Int = 0
    private var worker: Array<Worker?>
    private var barrier: CyclicBarrier?
    private var cancellation: CancellationToken = NoCancellation

    init {
        numThreads = 16
        worker = arrayOf()
        barrier = null
    }

    override fun render(
        film: IFilm,
        cancellation: CancellationToken,
    ) {
        this.cancellation = cancellation
        createWorkers(film)
        barrier = CyclicBarrier(numThreads + 1)
        parallel = numThreads > 1
        for (aWorker in worker) {
            aWorker?.film = film
            Thread(aWorker).start()
        }
        try {
            barrier?.await()
        } catch (e: InterruptedException) {
            // Restore the interrupt flag (we are consuming the exception) and surface the
            // failure instead of returning a half-rendered film.
            Thread.currentThread().interrupt()
            throw IllegalStateException(
                "ParallelRenderer was interrupted while awaiting worker completion " +
                    "for resolution ${film.resolution}",
                e,
            )
        } catch (e: BrokenBarrierException) {
            // A worker broke the barrier (interrupt/failure) before all parties arrived; the
            // render is incomplete, so propagate rather than swallow.
            throw IllegalStateException(
                "ParallelRenderer aborted: a worker failed before completing the render " +
                    "for resolution ${film.resolution}",
                e,
            )
        }
    }

    private fun createWorkers(film: IFilm) {
        val res = film.resolution
        val vertFactor = numThreads / 4
        if (res.height % vertFactor == 0) {
            worker = arrayOfNulls(numThreads)
            val yStep = film.resolution.height / vertFactor
            for (i in 0 until numThreads / 4) {
                worker[4 * i] = Worker(0, 1 * res.width / 4, i * yStep, (i + 1) * yStep)
                worker[4 * i + 1] = Worker(1 * res.width / 4, 2 * res.width / 4, i * yStep, (i + 1) * yStep)
                worker[4 * i + 2] = Worker(2 * res.width / 4, 3 * res.width / 4, i * yStep, (i + 1) * yStep)
                worker[4 * i + 3] = Worker(3 * res.width / 4, 4 * res.width / 4, i * yStep, (i + 1) * yStep)
            }
        } else {
            throw IllegalArgumentException(
                "ParallelRenderer requires the image height to be divisible by " +
                    "numThreads/4 (=$vertFactor): height ${res.height} is not, with " +
                    "numThreads=$numThreads",
            )
        }
    }

    private inner class Worker(
        private val xStart: Int,
        private val xEnd: Int,
        private val yStart: Int,
        private val yEnd: Int,
    ) : Runnable {
        var film: IFilm? = null

        override fun run() {
            var count = 0
            var r = yStart
            while (r < yEnd) {
                // Poll once per row so a cancelled render stops this worker's CPU work promptly; the
                // worker still reaches the barrier below so the master is released cleanly.
                if (cancellation.isCancelled) {
                    break
                }
                var c = xStart
                while (c < xEnd) {
                    val color = corrector.correct(render.render(r, c)).clamp()
                    film?.setPixel(c, r, color)
                    c += 1
                }
                count++
                r += 1
            }
            try {
                barrier?.await()
            } catch (e: InterruptedException) {
                // Restore the interrupt flag and break the barrier so the master thread is
                // released with a BrokenBarrierException instead of blocking forever.
                Thread.currentThread().interrupt()
                barrier?.reset()
            } catch (e: BrokenBarrierException) {
                // Another party already broke the barrier; the render is being aborted. The
                // master observes the same broken barrier and reports the failure, so this
                // worker simply stops rather than swallowing the failure silently.
                Logger.warn("ParallelRenderer worker stopping: render barrier was broken ($e)")
            }
        }
    }
}
