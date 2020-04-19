package net.dinkla.raytracer.gui

import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.gui.awt.Png
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.interfaces.jvm.getLogger
import net.dinkla.raytracer.world.WorldDefinition
import kotlin.system.exitProcess

object CommandLineUi {
    internal val LOGGER = getLogger(this::class.java)
}

fun main(args: Array<String>) {
    Counter.PAUSE = true

    if (args.size != 2) {
        throw RuntimeException("CommandLineUI expects input filename and output filename as arguments")
    }

    val fileNameIn = args[0]
    val fileNameOut = args[1]

    CommandLineUi.LOGGER.info("Rendering $fileNameIn to $fileNameOut")

    val worldDefinition: WorldDefinition? = worldDef(fileNameIn)
    if (null == worldDefinition) {
        CommandLineUi.LOGGER.warn("WorldDef $fileNameIn is not known")
        exitProcess(1)
    } else {
        Png.renderAndSave(worldDefinition, fileNameOut)
        Counter.stats(30)

        println("Hits")
        InnerNode.hits.println()

        println("fails")
        InnerNode.fails.println()
    }
}