package net.dinkla.raytracer.world

import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.StereoCamera
import net.dinkla.raytracer.cameras.StereoCompositor
import net.dinkla.raytracer.cameras.StereoViewing
import net.dinkla.raytracer.films.ColorGridFilm
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.renderer.SimpleSingleRayRenderer
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.utilities.Resolution

/**
 * Orchestrates a stereo render: the scene is rendered twice (once per eye) through the *existing*
 * single-ray pipeline and the two eye images are composited into one output [Film].
 *
 * This is render glue, not part of the testable core — the per-eye geometry lives in [StereoCamera]
 * and the pixel compositing in [StereoCompositor], both unit-tested. Here we only wire those together
 * with the renderer/tracer the [Context] supplies and is verified by rendering a stereo scene.
 */
internal object StereoRender {
    /**
     * Renders [world]'s [StereoCamera][World.stereoCamera] and returns the composite [Film]. The
     * world must already be adapted ([Context.adapt]) and initialized, so its [tracer][World.tracer]
     * is set. Each eye is rendered at the [context] resolution; the output is that resolution for
     * [StereoViewing.ANAGLYPH] and double-width for [StereoViewing.SIDE_BY_SIDE].
     */
    fun render(
        world: World,
        context: Context,
    ): Film {
        val stereo = requireNotNull(world.stereoCamera) { "StereoRender requires world.stereoCamera to be set" }
        val tracer = requireNotNull(world.tracer) { "World.tracer not set; context.adapt(world) must run first" }
        val eyeResolution = context.resolution

        val leftImage = renderEye(stereo.leftCamera(world.viewPlane), tracer, world, context, eyeResolution)
        val rightImage = renderEye(stereo.rightCamera(world.viewPlane), tracer, world, context, eyeResolution)

        val left = StereoCompositor.PixelSource(leftImage::colorAt)
        val right = StereoCompositor.PixelSource(rightImage::colorAt)
        val w = eyeResolution.width
        val h = eyeResolution.height

        return when (stereo.viewing) {
            StereoViewing.SIDE_BY_SIDE -> {
                val out = Film(Resolution(2 * w, h))
                StereoCompositor.sideBySide(left, right, w, h, out)
                out
            }
            StereoViewing.ANAGLYPH -> {
                val out = Film(Resolution(w, h))
                StereoCompositor.anaglyph(left, right, w, h, out)
                out
            }
        }
    }

    private fun renderEye(
        camera: Camera,
        tracer: Tracer,
        world: World,
        context: Context,
        resolution: Resolution,
    ): ColorGridFilm {
        val singleRayRenderer = SimpleSingleRayRenderer(camera.lens, tracer)
        val renderer = context.renderer(singleRayRenderer, world.viewPlane)
        val image = ColorGridFilm(resolution)
        renderer.render(image)
        return image
    }
}
