package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm
import org.apache.log4j.Logger

import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CyclicBarrier

class ParallelRenderer(protected val render: ISingleRayRenderer, protected val corrector: IColorCorrector) : IRenderer {

    var exposureTime = 1.0

    var parallel = false
    protected var numThreads: Int = 0
    protected var worker: Array<Worker?>
    protected var barrier: CyclicBarrier?

    init {
        numThreads = 16
        worker = arrayOf()
        barrier = null
    }

    override fun render(film: IFilm, frame: Int) {
        // Init
        createWorkers(film)
        barrier = CyclicBarrier(numThreads + 1)
        parallel = numThreads > 1

        // Work
        for (aWorker in worker) {
            aWorker?.film = film
            Thread(aWorker).start()
        }

        // Wait for workers to finish
        try {
            barrier?.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: BrokenBarrierException) {
            e.printStackTrace()
        }

    }

    protected fun createWorkers(film: IFilm) {
        val res = film.resolution
        val vertFactor = numThreads / 4
        if (res.vres % vertFactor == 0) {
            worker = arrayOfNulls(numThreads)
            val yStep = film.resolution.vres / vertFactor
            for (i in 0 until numThreads / 4) {
                worker[4 * i] = Worker(0, 1 * res.hres / 4, i * yStep, (i + 1) * yStep)
                worker[4 * i + 1] = Worker(1 * res.hres / 4, 2 * res.hres / 4, i * yStep, (i + 1) * yStep)
                worker[4 * i + 2] = Worker(2 * res.hres / 4, 3 * res.hres / 4, i * yStep, (i + 1) * yStep)
                worker[4 * i + 3] = Worker(3 * res.hres / 4, 4 * res.hres / 4, i * yStep, (i + 1) * yStep)
            }
        } else {
            throw RuntimeException("viewPlane.vres % numThreads != 0")
        }
    }

    /*
    protected void createWorkers(IFilm film) {
        final Resolution res = film.getResolution();
        if (res.vres % numThreads == 0) {
            worker = new Worker[numThreads];
            int yStep = film.getResolution().vres / (numThreads/2);
            for (int i = 0; i < numThreads / 2; i++) {
                worker[2*i] = new Worker(0, res.hres/2, i * yStep, (i + 1) * yStep);
                worker[2*i+1] = new Worker(res.hres/2, res.hres, i * yStep, (i + 1) * yStep);
            }
        } else {
            throw new RuntimeException("viewPlane.vres % numThreads != 0");
        }
    }
    */

    /*
    protected void createWorkers(IFilm film) {
        final Resolution res = film.getResolution();
        if (res.vres % numThreads == 0) {
            worker = new Worker[numThreads];
            int yStep = film.getResolution().vres / numThreads;
            for (int i = 0; i < numThreads; i++) {
                worker[i] = new Worker(0, res.hres, i * yStep, (i + 1) * yStep);
            }
        } else {
            throw new RuntimeException("viewPlane.vres % numThreads != 0");
        }
    }
*/

    protected inner class Worker(private val xStart: Int, private val xEnd: Int, private val yStart: Int, private val yEnd: Int) : Runnable {
        var film: IFilm? = null

        override fun run() {
            var count = 0
            val numLogLines = Math.max(25, (yEnd - yStart) / 10)
            var r = yStart
            while (r < yEnd) {
                if (count % numLogLines == 0) {
                    LOGGER.info("ParallelRender: " + count + " of " + (yEnd - yStart))
                }
                var c = xStart
                while (c < xEnd) {
                    var color = render.render(r, c)
                    color = color.times(exposureTime)
                    color = corrector.correct(color)
                    film!!.setPixel(0, c, r, color)
                    c += STEP_X
                }
                count++
                r += STEP_Y
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

    companion object {

        internal val LOGGER = Logger.getLogger(ParallelRenderer::class.java)

        private val STEP_X = 1
        private val STEP_Y = 1
    }

}