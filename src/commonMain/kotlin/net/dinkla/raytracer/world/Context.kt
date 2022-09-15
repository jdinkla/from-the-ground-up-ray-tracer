package net.dinkla.raytracer.world

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.cameras.render.IRenderer
import net.dinkla.raytracer.cameras.render.ISingleRayRenderer
import net.dinkla.raytracer.cameras.render.SimpleSingleRayRenderer
import net.dinkla.raytracer.tracers.Tracer

typealias TracerFactory = (IWorld) -> Tracer
typealias RendererFactory = (ISingleRayRenderer, IColorCorrector) -> IRenderer

class Context(val createTracer: TracerFactory, val createRenderer: RendererFactory) {
    fun adapt(world: World) {
        val theRealTracer = createTracer(world)
        world.tracer = theRealTracer

        val singleRayRenderer = SimpleSingleRayRenderer(world.camera!!.lens, theRealTracer)
        val corrector: IColorCorrector = world.viewPlane
        val renderer = Renderer()
        renderer.renderer = createRenderer(singleRayRenderer, corrector)
        world.renderer = renderer
    }
}