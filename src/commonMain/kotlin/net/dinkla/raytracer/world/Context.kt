package net.dinkla.raytracer.world

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.renderer.ISingleRayRenderer
import net.dinkla.raytracer.renderer.SimpleSingleRayRenderer
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.utilities.Resolution

typealias TracerCreator = (IWorld) -> Tracer
typealias RendererCreator = (ISingleRayRenderer, IColorCorrector) -> IRenderer

class Context(val tracer: TracerCreator, val renderer: RendererCreator, val resolution: Resolution) {
    fun adapt(world: World) {
        val theRealTracer = tracer(world)
        world.tracer = theRealTracer

        val singleRayRenderer = SimpleSingleRayRenderer(world.camera!!.lens, theRealTracer)
        val corrector: IColorCorrector = world.viewPlane
        world.renderer = renderer(singleRayRenderer, corrector)

        world.viewPlane.resolution = resolution
    }
}