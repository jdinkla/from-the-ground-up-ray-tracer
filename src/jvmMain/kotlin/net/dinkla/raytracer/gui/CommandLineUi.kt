package net.dinkla.raytracer.gui

import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.films.Render
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Counter.PAUSE = true

    if (args.size != 2) {
        throw RuntimeException("CommandLineUI expects input filename and output filename as arguments")
    }

    val fileNameIn = args[0]
    val fileNameOut = args[1]

    Logger.info("Rendering $fileNameIn to $fileNameOut")

    val worldDefinition = worldDef(fileNameIn)
    if (null == worldDefinition) {
        Logger.warn("WorldDef $fileNameIn is not known")
        exitProcess(1)
    } else {
        val (film, _) = Render.render(worldDefinition)
        film.save(fileNameOut)
        Counter.stats(30)

        println("Hits")
        InnerNode.hits.println()

        println("fails")
        InnerNode.fails.println()
    }
}