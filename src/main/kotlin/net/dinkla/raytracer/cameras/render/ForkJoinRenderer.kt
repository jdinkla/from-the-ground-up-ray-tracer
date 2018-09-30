package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.films.IFilm

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction

class ForkJoinRenderer(protected val render: ISingleRayRenderer, protected val corrector: IColorCorrector) : IRenderer {

    protected var numThreads: Int = 0
    protected var sizeGrid: Int = 0
    private var pool: ForkJoinPool? = null

    var exposureTime = 1.0

    protected var film: IFilm? = null

    init {
        numThreads = 16
        sizeGrid = 4
        val procs = Runtime.getRuntime().availableProcessors()
        pool = ForkJoinPool(procs)
    }

    override fun render(film: IFilm) {
        this.film = film
        val res = film.resolution

        val master = Master(sizeGrid, res.hres, res.vres)
        pool!!.invoke(master)
        pool!!.shutdown()
        this.film = null
    }

    internal inner class Master(val numBlocks: Int, width: Int, height: Int) : RecursiveAction() {
        val blockHeight: Int
        val blockWidth: Int
        val blocks: Int
        val offset: Int

        var actions: Array<Worker?>

        init {
            blockHeight = height / numBlocks
            blockWidth = width / numBlocks
            blocks = numBlocks * numBlocks
            offset = 256 / blocks
            actions = arrayOfNulls(blocks)
            //System.out.println("blocks=" + blocks);
        }

        override fun compute() {
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
        }

    }

    /**
     *
     */
    internal inner class Worker(private val xStart: Int, private val xEnd: Int, private val yStart: Int, private val yEnd: Int) : RecursiveAction() {

        override fun compute() {
            var count = 0
            val numLogLines = Math.max(25, (yEnd - yStart) / 10)
            var r = yStart
            while (r < yEnd) {
                var c = xStart
                while (c < xEnd) {
                    var color = render.render(r, c)
                    color = color.times(exposureTime)
                    color = corrector.correct(color)
                    film!!.setPixel(c, r, color.clamp())
                    c += STEP_X
                }
                count++
                r += STEP_Y
            }
        }
    }

    companion object {

        private val STEP_X = 1
        private val STEP_Y = 1
    }
}
