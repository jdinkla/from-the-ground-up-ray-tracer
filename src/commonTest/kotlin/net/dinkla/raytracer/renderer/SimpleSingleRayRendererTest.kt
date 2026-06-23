package net.dinkla.raytracer.renderer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
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

    "throws a contextual error when the lens yields no ray for a pixel" {
        val renderer = SimpleSingleRayRenderer(FixedLens(null), RecordingTracer(Color.WHITE))

        val ex =
            shouldThrow<IllegalArgumentException> {
                renderer.render(r = 1, c = 2)
            }

        ex.message shouldContain "(1, 2)"
    }
})
