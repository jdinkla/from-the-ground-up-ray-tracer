package net.dinkla.raytracer.gui

import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.world.WorldDef
import org.slf4j.LoggerFactory
import java.lang.System.exit

object CommandLineUi {

    internal val LOGGER = LoggerFactory.getLogger(this::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        Counter.PAUSE = true

        if (args.size != 2) {
            throw RuntimeException("CommandLineUI expects input filename and output filename as arguments")
        }

        val fileNameIn = args[0]
        val fileNameOut = args[1]

        LOGGER.info("Rendering $fileNameIn to $fileNameOut")

        val wdef: WorldDef? = worldDef(fileNameIn)
        if (null == wdef) {
            LOGGER.warn("WorldDef $fileNameIn is not known")
            exit(1)
        } else {
            Png.renderAndSave(wdef, fileNameOut)
            Counter.stats(30)

            println("Hits")
            InnerNode.hits.println()

            println("fails")
            InnerNode.fails.println()
        }
    }


}
