package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CyclicBarrier

class ParallelRenderer(private val render: ISingleRayRenderer, private val corrector: IColorCorrector) : IRenderer {
    private var parallel = false
    private var numThreads: Int = 0
    private var worker: Array<Worker?>
    private var barrier: CyclicBarrier?

    init {
        numThreads = 16
        worker = arrayOf()
        barrier = null
    }

    override fun render(film: IFilm) {
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
            e.printStackTrace()
        } catch (e: BrokenBarrierException) {
            e.printStackTrace()
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
            throw RuntimeException("viewPlane.vres % numThreads != 0")
        }
    }

    private inner class Worker(
        private val xStart: Int, private val xEnd: Int, private val yStart: Int, private val yEnd: Int
    ) : Runnable {
        var film: IFilm? = null
        override fun run() {
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
            try {
                barrier?.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: BrokenBarrierException) {
                e.printStackTrace()
            }
        }
    }
}
