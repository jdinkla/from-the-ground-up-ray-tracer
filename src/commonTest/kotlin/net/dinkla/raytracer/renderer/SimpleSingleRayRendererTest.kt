package net.dinkla.raytracer.renderer

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.cameras.lenses.ILens
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer

// A lens that hands the single-ray path a fixed ray (or null, to exercise the guard). The sampled
// path is unused by SimpleSingleRayRenderer and errors if reached.
private class FixedLens(
    private val ray: Ray?,
) : ILens {
    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray? = ray

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray = error("sampled path must not be used by the simple single-ray renderer")
}

// Records the depth it was traced with and returns a fixed colour, so the renderer's contract
// (single primary ray at depth 0) is observable.
private class RecordingTracer(
    private val color: Color,
) : Tracer {
    var lastDepth: Int = -1

    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color {
        lastDepth = depth
        return color
    }
}

class SimpleSingleRayRendererTest : StringSpec({

    val ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))

    "returns the tracer's colour for the lens's primary ray at depth 0" {
        val tracer = RecordingTracer(Color(0.2, 0.4, 0.6))
        val renderer = SimpleSingleRayRenderer(FixedLens(ray), tracer)

        val color = renderer.render(r = 3, c = 7)

        color shouldBeApprox Color(0.2, 0.4, 0.6)
        tracer.lastDepth shouldBe 0
    }

    "returns the background (black) and does not trace when the lens yields no ray for a pixel" {
        val tracer = RecordingTracer(Color.WHITE)
        val renderer = SimpleSingleRayRenderer(FixedLens(null), tracer)

        val color = renderer.render(r = 1, c = 2)

        color shouldBe Color.BLACK
        tracer.lastDepth shouldBe -1 // tracer was never invoked
    }

    "leaves the traced colour unchanged at the default exposureTime of 1.0" {
        // The exposureTime default must be a no-op so every existing scene is byte-identical (AC#1).
        val tracer = RecordingTracer(Color(0.2, 0.4, 0.6))
        val renderer = SimpleSingleRayRenderer(FixedLens(ray), tracer)

        val color = renderer.render(r = 3, c = 7)

        color shouldBeApprox Color(0.2, 0.4, 0.6)
    }

    "scales the traced radiance by a reduced exposureTime" {
        // exposureTime = 0.25 darkens every channel uniformly: 0.8 * 0.25 = 0.2, etc.
        val tracer = RecordingTracer(Color(0.8, 0.4, 0.2))
        val renderer = SimpleSingleRayRenderer(FixedLens(ray), tracer, exposureTime = 0.25)

        val color = renderer.render(r = 3, c = 7)

        color shouldBeApprox Color(0.2, 0.1, 0.05)
    }
})
