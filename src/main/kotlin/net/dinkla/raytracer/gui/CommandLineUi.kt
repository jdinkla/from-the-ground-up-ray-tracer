package net.dinkla.raytracer.gui

import net.dinkla.raytracer.films.BufferedImageFilm
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.films.PngFilm
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.worlds.World
import net.dinkla.raytracer.worlds.WorldBuilder
import org.apache.log4j.Logger
import java.io.File

object CommandLineUi {

    internal val LOGGER = Logger.getLogger(CommandLineUi::class.java)

    @JvmStatic
    fun main(args: Array<String>) {

        Counter.PAUSE = true

        if (args.size != 2) {
            throw RuntimeException("CommandLineUI expects input filename and output filename as arguments")
        }

        val fileNameIn = args[0]
        val fileNameOut = args[1]

        LOGGER.info("Rendering $fileNameIn to $fileNameOut")

        val file = File(fileNameIn)
        val w: World = WorldBuilder.create(file)
        w.initialize()

        val png = PngFilm(BufferedImageFilm(w.viewPlane.resolution))
        w.camera!!.render(png as IFilm, 0)
        png.saveAsPng(fileNameOut)

        Counter.stats(30)

        println("Hits")
        InnerNode.hits.println()

        println("fails")
        InnerNode.fails.println()

    }

}
