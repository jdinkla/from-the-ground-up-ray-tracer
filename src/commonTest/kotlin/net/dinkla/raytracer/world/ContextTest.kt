package net.dinkla.raytracer.world

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.renderer.ISingleRayRenderer
import net.dinkla.raytracer.renderer.SampledSingleRayRenderer
import net.dinkla.raytracer.renderer.SimpleSingleRayRenderer
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.utilities.Resolution

// Counts how often it is asked to trace, so a sampled render of one pixel reveals how many primary
// samples the wired renderer casts — i.e. the numSamples it was built with.
private class CountingTracer : Tracer {
    var traces: Int = 0

    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color {
        traces++
        return Color.BLACK
    }
}

// A no-op renderer; Context wraps the chosen single-ray renderer in one of these via the creator.
private class NoopRenderer : IRenderer {
    override fun render(film: IFilm) = Unit
}

// Builds a minimal pinhole world (optionally with anti-aliasing samples) and runs Context.adapt
// against it with a captured renderer creator, returning the single-ray renderer Context selected
// and the tracer it traced through.
private fun adaptAndCapture(numSamples: Int? = null): Pair<ISingleRayRenderer, CountingTracer> {
    val tracer = CountingTracer()
    val world =
        Builder.build {
            camera()
            if (numSamples != null) {
                samples(numSamples)
            }
        }
    var captured: ISingleRayRenderer? = null
    val context =
        Context(
            tracer = { tracer },
            renderer = { single, _ ->
                captured = single
                NoopRenderer()
            },
            resolution = Resolution(width = 4, height = 4),
        )

    context.adapt(world)

    return requireNotNull(captured) { "Context did not build a single-ray renderer" } to tracer
}

class ContextTest : StringSpec({

    "selects the simple single-ray renderer when numSamples is 1 (the default, no anti-aliasing)" {
        val (single, _) = adaptAndCapture(numSamples = 1)

        single.shouldBeInstanceOf<SimpleSingleRayRenderer>()
    }

    "defaults to the simple single-ray renderer when no samples are requested" {
        val (single, _) = adaptAndCapture()

        single.shouldBeInstanceOf<SimpleSingleRayRenderer>()
    }

    "selects the sampled single-ray renderer when numSamples is greater than 1" {
        val (single, _) = adaptAndCapture(numSamples = 8)

        single.shouldBeInstanceOf<SampledSingleRayRenderer>()
    }

    "wires the sampled renderer with the view plane's sample count" {
        // Rendering one pixel must cast exactly numSamples primary rays, proving the view-plane
        // numSamples (not some hardcoded default) drives the wired renderer.
        val numSamples = 12
        val (single, tracer) = adaptAndCapture(numSamples = numSamples)

        single.render(r = 0, c = 0)

        tracer.traces shouldBe numSamples
    }

    "the single-sample path traces the pixel exactly once (byte-identical single-ray behaviour)" {
        val (single, tracer) = adaptAndCapture(numSamples = 1)

        single.render(r = 2, c = 2)

        tracer.traces shouldBe 1
    }
})
