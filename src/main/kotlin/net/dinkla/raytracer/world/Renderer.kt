package net.dinkla.raytracer.world

import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.render.IRenderer
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Timer
import org.slf4j.LoggerFactory

class Renderer {

    var tracer: Tracer? = null
    var camera: Camera? = null
    var renderer: IRenderer? = null

    fun render(film: Film) {
        assert(null != renderer)
        assert(null != tracer)
        assert(null != camera)

        val timer = Timer()
        timer.start()
        renderer?.render(film)
        timer.stop()
        LOGGER.info("rendering took " + timer.duration + " ms")

        // stats
        Counter.stats(30)

        println("Hits")
        InnerNode.hits.println()

        println("fails")
        InnerNode.fails.println()

        println("took " + timer.duration + " [ms]")
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}