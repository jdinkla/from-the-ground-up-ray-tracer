package net.dinkla.raytracer.renderer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution
import java.util.Collections

private class RecordingFilm(
    override val resolution: Resolution,
) : IFilm {
    val writes: MutableList<Pair<Int, Int>> = Collections.synchronizedList(mutableListOf())

    // Records the last colour written per (x, y) so equivalence tests can compare full images.
    val pixels: MutableMap<Pair<Int, Int>, Color> = Collections.synchronizedMap(mutableMapOf())

    override fun setPixel(
        x: Int,
        y: Int,
        color: Color,
    ) {
        writes.add(x to y)
        pixels[x to y] = color
    }
}

private class StubSingleRayRenderer(
    private val color: Color,
) : ISingleRayRenderer {
    override fun render(
        r: Int,
        c: Int,
    ): Color = color
}

// Returns a distinct colour per (row, column) so an equivalence test detects a renderer that
// writes the right number of pixels but to the wrong coordinates. A constant colour would not.
private object PositionalSingleRayRenderer : ISingleRayRenderer {
    override fun render(
        r: Int,
        c: Int,
    ): Color = Color(r.toDouble() / 1000.0, c.toDouble() / 1000.0, 0.0)
}

private object IdentityCorrector : IColorCorrector {
    override fun correct(color: Color): Color = color
}

private fun fullCoverage(film: RecordingFilm): Set<Pair<Int, Int>> =
    buildSet {
        for (y in 0 until film.resolution.height) {
            for (x in 0 until film.resolution.width) {
                add(x to y)
            }
        }
    }

class RendererTest : StringSpec({

    // ---------------------------------------------------------------------------------------
    // ParallelRenderer (pre-existing, hardened in TASK-5)
    // ---------------------------------------------------------------------------------------

    "parallel renderer fills all pixels" {
        val film = RecordingFilm(Resolution(width = 4, height = 4))
        val renderer = ParallelRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.writes.size shouldBe film.resolution.width * film.resolution.height
    }

    "parallel renderer fails on incompatible resolution with a contextual IllegalArgumentException" {
        val film = RecordingFilm(Resolution(width = 3, height = 5))
        val renderer = ParallelRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        val ex =
            shouldThrow<IllegalArgumentException> {
                renderer.render(film)
            }

        ex.message shouldContain "ParallelRenderer"
        ex.message shouldContain "height"
    }

    // ---------------------------------------------------------------------------------------
    // ForkJoinRenderer  (block-based, NUMBER_OF_BLOCKS = 8)
    // ---------------------------------------------------------------------------------------

    "fork-join renderer fills every pixel of a block-aligned film" {
        // 8x8 is divisible by the 8x8 block grid, so every pixel falls inside a block.
        val film = RecordingFilm(Resolution(width = 8, height = 8))
        val renderer = ForkJoinRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
    }

    "fork-join renderer silently under-renders a film smaller than its block grid" {
        // 4x4 is smaller than the 8x8 block grid, so integer block sizes round down to zero and
        // no pixel is written. This is the renderer's deterministic degradation (it has no guard
        // that throws), so the test pins the observable shortfall rather than a typed exception.
        val film = RecordingFilm(Resolution(width = 4, height = 4))
        val renderer = ForkJoinRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.writes.size shouldBe 0
    }

    // ---------------------------------------------------------------------------------------
    // CoroutineBlockRenderer  (block-based, NUMBER_OF_BLOCKS = 32)
    // ---------------------------------------------------------------------------------------

    "coroutine block renderer fills every pixel of a block-aligned film" {
        val film = RecordingFilm(Resolution(width = 32, height = 32))
        val renderer = CoroutineBlockRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
    }

    "coroutine block renderer silently under-renders a film smaller than its block grid" {
        val film = RecordingFilm(Resolution(width = 8, height = 8))
        val renderer = CoroutineBlockRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.writes.size shouldBe 0
    }

    // ---------------------------------------------------------------------------------------
    // NaiveCoroutineRenderer  (per-pixel, no block grid, no resolution guard)
    // ---------------------------------------------------------------------------------------

    "naive coroutine renderer fills all pixels" {
        val film = RecordingFilm(Resolution(width = 5, height = 7))
        val renderer = NaiveCoroutineRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
    }

    "naive coroutine renderer fully renders a film that the block renderers leave empty" {
        // The naive renderer has no block grid, so unlike the block renderers it renders an 8x8
        // film completely. This is the behavioural counterpart to the block renderers' shortfall.
        val film = RecordingFilm(Resolution(width = 8, height = 8))
        val renderer = NaiveCoroutineRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.writes.size shouldBe film.resolution.width * film.resolution.height
    }

    // ---------------------------------------------------------------------------------------
    // VirtualThreadBlockRenderer  (block-based, NUMBER_OF_BLOCKS = 32)
    // ---------------------------------------------------------------------------------------

    "virtual thread renderer fills every pixel of a block-aligned film" {
        val film = RecordingFilm(Resolution(width = 32, height = 32))
        val renderer = VirtualThreadBlockRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
    }

    "virtual thread renderer silently under-renders a film smaller than its block grid" {
        val film = RecordingFilm(Resolution(width = 8, height = 8))
        val renderer = VirtualThreadBlockRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.writes.size shouldBe 0
    }

    // ---------------------------------------------------------------------------------------
    // AC#3 — every renderer strategy produces the same image for a reference scene
    // ---------------------------------------------------------------------------------------

    "all renderer strategies produce identical pixels for a reference film" {
        // 32x32 is divisible by every renderer's block grid (1, 8, 32, 4-quarters), so each one
        // covers the whole film and the comparison is over a complete image.
        val resolution = Resolution(width = 32, height = 32)

        fun renderWith(renderer: IRenderer): Map<Pair<Int, Int>, Color> {
            val film = RecordingFilm(resolution)
            renderer.render(film)
            return film.pixels.toMap()
        }

        val reference = renderWith(SequentialRenderer(PositionalSingleRayRenderer, IdentityCorrector))

        val strategies: List<IRenderer> =
            listOf(
                ForkJoinRenderer(PositionalSingleRayRenderer, IdentityCorrector),
                ParallelRenderer(PositionalSingleRayRenderer, IdentityCorrector),
                NaiveCoroutineRenderer(PositionalSingleRayRenderer, IdentityCorrector),
                CoroutineBlockRenderer(PositionalSingleRayRenderer, IdentityCorrector),
                VirtualThreadBlockRenderer(PositionalSingleRayRenderer, IdentityCorrector),
            )

        // Sanity: the reference itself must be a complete image, else equivalence is vacuous.
        reference.size shouldBe resolution.width * resolution.height

        for (strategy in strategies) {
            renderWith(strategy) shouldBe reference
        }
    }
})
