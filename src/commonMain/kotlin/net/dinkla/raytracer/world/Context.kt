package net.dinkla.raytracer.world

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.renderer.ISingleRayRenderer
import net.dinkla.raytracer.renderer.SimpleSingleRayRenderer
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.utilities.Resolution

typealias TracerFactory = (IWorld) -> Tracer
typealias RendererFactory = (ISingleRayRenderer, IColorCorrector) -> IRenderer

class Context(val createTracer: TracerFactory, val createRenderer: RendererFactory, val resolution: Resolution) {
    fun adapt(world: World) {
        val theRealTracer = createTracer(world)
        world.tracer = theRealTracer

        val singleRayRenderer = SimpleSingleRayRenderer(world.camera!!.lens, theRealTracer)
        val corrector: IColorCorrector = world.viewPlane
        world.renderer = createRenderer(singleRayRenderer, corrector)

        world.viewPlane.resolution = resolution
    }
}