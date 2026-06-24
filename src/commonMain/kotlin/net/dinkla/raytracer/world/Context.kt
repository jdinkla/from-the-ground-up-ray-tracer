package net.dinkla.raytracer.world

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.renderer.ISingleRayRenderer
import net.dinkla.raytracer.renderer.SampledSingleRayRenderer
import net.dinkla.raytracer.renderer.SimpleSingleRayRenderer
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.utilities.Resolution

typealias TracerCreator = (IWorld) -> Tracer
typealias RendererCreator = (ISingleRayRenderer, IColorCorrector) -> IRenderer

class Context(
    val tracer: TracerCreator,
    val renderer: RendererCreator,
    val resolution: Resolution,
) {
    fun adapt(world: World) {
        val theRealTracer = tracer(world)
        world.tracer = theRealTracer

        val singleRayRenderer = singleRayRenderer(world, theRealTracer)
        val corrector: IColorCorrector = world.viewPlane
        world.renderer = renderer(singleRayRenderer, corrector)

        // Preserve the scene's field of view across resolutions: applyResolution rescales the pixel
        // size so changing the resolution only changes sampling density, not the framing (TASK-36).
        world.viewPlane.applyResolution(resolution)
    }

    /**
     * Selects the per-pixel render strategy from the scene's `ViewPlane.numSamples`: more than one
     * sample opts the scene into multi-sample anti-aliasing (and thin-lens depth-of-field blur) via
     * [SampledSingleRayRenderer]; the default of one sample keeps the historical single-ray,
     * no-anti-aliasing behaviour via [SimpleSingleRayRenderer] — byte-identical to before this wiring.
     */
    private fun singleRayRenderer(
        world: World,
        theRealTracer: Tracer,
    ): ISingleRayRenderer {
        val numSamples = world.viewPlane.numSamples
        val exposureTime = world.camera.exposureTime
        return if (numSamples > 1) {
            SampledSingleRayRenderer(world.camera.lens, theRealTracer, numSamples, exposureTime = exposureTime)
        } else {
            SimpleSingleRayRenderer(world.camera.lens, theRealTracer, exposureTime)
        }
    }
}
