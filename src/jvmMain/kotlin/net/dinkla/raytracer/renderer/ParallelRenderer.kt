package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Logger
import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicReference

class ParallelRenderer(
    private val render: ISingleRayRenderer,
    private val corrector: IColorCorrector,
) : IRenderer {
    private var parallel = false
    private var numThreads: Int = 0
    private var worker: Array<Worker?>
    private var barrier: CyclicBarrier?
    private var cancellation: CancellationToken = NoCancellation

    // The first pixel-loop failure thrown by any worker (e.g. an incompatible tracer raising
    // UnsupportedOperationException). First write wins via compareAndSet; the master rethrows it after
    // the barrier so render(film) fails fast with the original cause instead of deadlocking (TASK-45).
    private val workerFailure = AtomicReference<Throwable?>(null)

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
        workerFailure.set(null)
        createWorkers(film)
        barrier = CyclicBarrier(numThreads + 1)
        parallel = numThreads > 1
        for (aWorker in worker) {
            aWorker?.film = film
            // Daemon so an unexpected stuck worker can never keep the JVM (or a CI test process) alive:
            // these threads are owned entirely by this render() call and must not outlive it.
            Thread(aWorker).apply { isDaemon = true }.start()
        }
        awaitWorkers(film)
    }

    /**
     * Blocks on the barrier until every worker arrives, then surfaces any worker pixel-loop failure.
     * Each worker reaches the barrier even after failing (its `finally`), so the master is released
     * cleanly instead of deadlocking; a recorded failure is then rethrown with the original cause
     * attached so `render(film)` fails fast. A cancelled render records nothing, so this returns
     * normally (cancelled, no throw).
     */
    private fun awaitWorkers(film: IFilm) {
        try {
            barrier?.await()
        } catch (e: InterruptedException) {
            // Restore the interrupt flag (we are consuming the exception); the throw below surfaces the
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
        // Every worker reached the barrier (the finally in Worker.run guarantees it even after a
        // failure), so the master is released cleanly. Surface any recorded pixel-loop failure with the
        // original cause attached; a cancelled render records nothing, so this returns normally.
        rethrowWorkerFailure(film)
    }

    private fun rethrowWorkerFailure(film: IFilm) {
        workerFailure.get()?.let { failure ->
            throw IllegalStateException(
                "ParallelRenderer aborted: a worker failed while rendering " +
                    "resolution ${film.resolution}",
                failure,
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

        // Broad catch is deliberate: a pixel shade can fail in many unrelated ways (an incompatible
        // tracer raising UnsupportedOperationException, arithmetic, IO). We must not let the throwable
        // escape run(), or this worker would die before the barrier and deadlock the master. Instead we
        // record the first failure and always reach the barrier in the finally; the master rethrows it.
        @Suppress("TooGenericExceptionCaught")
        override fun run() {
            try {
                renderRows()
            } catch (e: Throwable) {
                // First failure wins; later workers' failures are dropped. Record before the finally so
                // the master sees it once the barrier releases.
                workerFailure.compareAndSet(null, e)
            } finally {
                awaitBarrier()
            }
        }

        private fun renderRows() {
            var r = yStart
            while (r < yEnd) {
                // Poll once per row so a cancelled render stops this worker's CPU work promptly; the
                // worker still reaches the barrier in the finally so the master is released cleanly.
                if (cancellation.isCancelled) {
                    break
                }
                var c = xStart
                while (c < xEnd) {
                    val color = corrector.correct(render.render(r, c)).clamp()
                    film?.setPixel(c, r, color)
                    c += 1
                }
                r += 1
            }
        }

        private fun awaitBarrier() {
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
