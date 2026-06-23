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

// A lens that ignores the pixel/jitter and instead encodes its own invocation index into the ray
// origin's x. The renderer's per-pixel sample loop calls getRaySampled exactly numSamples times, so
// the index sequence (0, 1, 2, ...) is deterministic regardless of the (random) jitter point — which
// lets the averaging test assert the exact mean without depending on the unseeded sampler.
private class CountingLens : ILens {
    var calls: Int = 0

    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray = error("single-ray path must not be used by the sampled renderer")

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray = Ray(Point3D(calls++.toDouble(), 0.0, 0.0), Vector3D(0.0, 0.0, -1.0))
}

// Always returns the same colour, so N samples average back to that colour exactly.
private class ConstantTracer(
    private val color: Color,
) : Tracer {
    var traces: Int = 0

    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color {
        traces++
        return color
    }
}

// A lens whose sampled path yields no ray for any pixel, exercising the renderer's requireNotNull guard.
private object NullSampledLens : ILens {
    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray = error("single-ray path must not be used by the sampled renderer")

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray? = null
}

// Returns a colour derived from the ray-origin index the CountingLens stamped in (red = index/100),
// so each of the N samples contributes a distinct, deterministic value and the renderer's mean can be
// checked against the closed-form average of 0..N-1.
private object IndexedTracer : Tracer {
    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color = Color(ray.origin.x / 100.0, 0.0, 0.0)
}

class SampledSingleRayRendererTest : StringSpec({

    "averages a constant tracer back to that colour over many samples" {
        val color = Color(0.2, 0.4, 0.6)
        val renderer = SampledSingleRayRenderer(CountingLens(), ConstantTracer(color), numSamples = 16)

        val result = renderer.render(r = 3, c = 7)

        result shouldBeApprox color
    }

    "casts exactly numSamples rays per pixel" {
        val tracer = ConstantTracer(Color.WHITE)
        val renderer = SampledSingleRayRenderer(CountingLens(), tracer, numSamples = 9)

        renderer.render(r = 0, c = 0)

        tracer.traces shouldBe 9
    }

    "divides the accumulated colour by the sample count (mean of per-sample values)" {
        // The CountingLens stamps indices 0..N-1 into the ray origins and IndexedTracer maps each to
        // red = index/100, so the mean red is (1/N) * sum(0..N-1)/100 = ((N-1)/2)/100. For N=10 that
        // is 4.5/100 = 0.045 — exact, independent of the random in-pixel jitter, pinning the /N.
        val n = 10
        val renderer = SampledSingleRayRenderer(CountingLens(), IndexedTracer, numSamples = n)

        val result = renderer.render(r = 1, c = 1)

        result shouldBeApprox Color(((n - 1) / 2.0) / 100.0, 0.0, 0.0)
    }

    "rejects a non-positive sample count" {
        shouldThrow<IllegalArgumentException> {
            SampledSingleRayRenderer(CountingLens(), ConstantTracer(Color.WHITE), numSamples = 0)
        }
    }

    "throws a contextual error when the lens yields no ray for a sampled pixel" {
        val renderer = SampledSingleRayRenderer(NullSampledLens, ConstantTracer(Color.WHITE), numSamples = 4)

        val ex =
            shouldThrow<IllegalArgumentException> {
                renderer.render(r = 2, c = 5)
            }

        ex.message shouldContain "(2, 5)"
    }
})
