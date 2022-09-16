import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.renderer.Renderers
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

fun main() {
    val args = listOf("World66.kt", "out.png")
    if (args.size != 2) {
        Logger.error("CommandLineUI expects input filename and output filename as arguments")
        return
    }
    val fileNameIn = args[0]
    val fileNameOut = args[1]
    Logger.info("Rendering $fileNameIn to $fileNameOut")
    val worldDefinition = worldDef(fileNameIn)
    if (null == worldDefinition) {
        Logger.warn("WorldDef $fileNameIn is not known")
    } else {
        Logger.info("Using world ${worldDefinition.world().id}")
        val context = Context(Tracers.WHITTED.create, Renderers.SEQUENTIAL.create, Resolution.RESOLUTION_720)
        val (film, _) = Render.render(worldDefinition, context)
        film.save(fileNameOut)
        Counter.stats(30)

        println("Hits")
        InnerNode.hits.println()

        println("fails")
        InnerNode.fails.println()
    }
}