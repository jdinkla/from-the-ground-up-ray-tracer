package net.dinkla.raytracer.renderer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

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

/**
 * A film that requests cancellation through [token] once [cancelAfter] pixels have been written, and
 * keeps counting every subsequent write. A cancellation-aware renderer should stop polling its token
 * shortly after that threshold, so the final count stays well below the full image — proving the
 * renderer returned early rather than running to completion. The count uses an [AtomicInteger] because
 * the parallel renderers call [setPixel] from many threads.
 */
private class CancellingFilm(
    override val resolution: Resolution,
    private val cancelAfter: Int,
    private val token: AtomicCancellationToken,
) : IFilm {
    private val written = AtomicInteger(0)

    val pixelsWritten: Int
        get() = written.get()

    override fun setPixel(
        x: Int,
        y: Int,
        color: Color,
    ) {
        val count = written.incrementAndGet()
        if (count >= cancelAfter) {
            token.cancel()
        }
    }
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

    "fork-join renderer fills every pixel of a film smaller than its block grid" {
        // 4x4 is smaller than the 8x8 block grid. Before TASK-25 the integer block size rounded
        // down to zero and nothing was written; the partition fix now caps the block count at the
        // dimension so every pixel is covered exactly once.
        val film = RecordingFilm(Resolution(width = 4, height = 4))
        val renderer = ForkJoinRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
        film.writes.size shouldBe film.resolution.width * film.resolution.height
    }

    "fork-join renderer fills every pixel of a non-divisible film" {
        // 10x7 divides unevenly into the 8x8 grid; the remainder rows/columns must be rendered,
        // not dropped, and no pixel written twice.
        val film = RecordingFilm(Resolution(width = 10, height = 7))
        val renderer = ForkJoinRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
        film.writes.size shouldBe film.resolution.width * film.resolution.height
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

    "coroutine block renderer fills every pixel of a film smaller than its block grid" {
        // 8x8 is smaller than the 32x32 block grid; before TASK-25 it wrote zero pixels.
        val film = RecordingFilm(Resolution(width = 8, height = 8))
        val renderer = CoroutineBlockRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
        film.writes.size shouldBe film.resolution.width * film.resolution.height
    }

    "coroutine block renderer fills every pixel of a non-divisible film" {
        val film = RecordingFilm(Resolution(width = 50, height = 33))
        val renderer = CoroutineBlockRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
        film.writes.size shouldBe film.resolution.width * film.resolution.height
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

    "naive coroutine renderer fully renders an 8x8 film" {
        // The naive renderer has no block grid and always renders the whole film. Since TASK-25
        // the block renderers also cover an 8x8 film completely, so this is now a plain coverage
        // check rather than a contrast against the block renderers' former shortfall.
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

    "virtual thread renderer fills every pixel of a film smaller than its block grid" {
        // 8x8 is smaller than the 32x32 block grid; before TASK-25 it wrote zero pixels.
        val film = RecordingFilm(Resolution(width = 8, height = 8))
        val renderer = VirtualThreadBlockRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
        film.writes.size shouldBe film.resolution.width * film.resolution.height
    }

    "virtual thread renderer fills every pixel of a non-divisible film" {
        val film = RecordingFilm(Resolution(width = 50, height = 33))
        val renderer = VirtualThreadBlockRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.pixels.keys shouldBe fullCoverage(film)
        film.writes.size shouldBe film.resolution.width * film.resolution.height
    }

    // ---------------------------------------------------------------------------------------
    // AC#3 — every renderer strategy produces the same image for a reference scene
    // ---------------------------------------------------------------------------------------

    "all renderer strategies produce identical pixels for a block-aligned reference film" {
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

    "block renderers agree with the sequential reference for a non-divisible film" {
        // 10x7 divides evenly into no renderer's block grid (8 or 32) and is smaller than the
        // coroutine/virtual-thread 32x32 grid. After TASK-25 each block renderer must still
        // reproduce the sequential image pixel-for-pixel, proving the partition fix covers the
        // remainder rows/columns at the right coordinates (not merely the right pixel count).
        val resolution = Resolution(width = 10, height = 7)

        fun renderWith(renderer: IRenderer): Map<Pair<Int, Int>, Color> {
            val film = RecordingFilm(resolution)
            renderer.render(film)
            return film.pixels.toMap()
        }

        val reference = renderWith(SequentialRenderer(PositionalSingleRayRenderer, IdentityCorrector))

        val strategies: List<IRenderer> =
            listOf(
                ForkJoinRenderer(PositionalSingleRayRenderer, IdentityCorrector),
                NaiveCoroutineRenderer(PositionalSingleRayRenderer, IdentityCorrector),
                CoroutineBlockRenderer(PositionalSingleRayRenderer, IdentityCorrector),
                VirtualThreadBlockRenderer(PositionalSingleRayRenderer, IdentityCorrector),
            )

        // The reference must be a complete image, else equivalence is vacuous. ParallelRenderer is
        // intentionally excluded: it guards 10x7 (height not divisible by numThreads/4) by design.
        reference.size shouldBe resolution.width * resolution.height

        for (strategy in strategies) {
            renderWith(strategy) shouldBe reference
        }
    }

    // ---------------------------------------------------------------------------------------
    // AC#1/#2 — cooperative cancellation: a render cancelled partway returns early without
    // writing every pixel. The film flips the token after `cancelAfter` writes; a renderer that
    // ignored the token would run to completion and write all width*height pixels. We assert the
    // final count is strictly below the full image, which fails if the renderer never checks.
    // A large film keeps a meaningful gap between the cancel threshold and the total even for the
    // block/thread renderers, where blocks already in flight when the token flips still finish.
    // ---------------------------------------------------------------------------------------

    // 256x256 = 65536 pixels; cancel after 64 so even the coarsest (per-block) checker stops far
    // short of the full image. Chosen large enough that already-running blocks can't fill it.
    val cancelResolution = Resolution(width = 256, height = 256)
    val cancelAfter = 64
    val totalPixels = cancelResolution.width * cancelResolution.height

    fun cancellationStopsEarly(makeRenderer: (ISingleRayRenderer, IColorCorrector) -> IRenderer): Pair<Int, Int> {
        val token = AtomicCancellationToken()
        val film = CancellingFilm(cancelResolution, cancelAfter, token)
        val renderer = makeRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film, token)

        return film.pixelsWritten to totalPixels
    }

    "sequential renderer stops early when cancelled partway" {
        val (written, total) = cancellationStopsEarly(::SequentialRenderer)

        written shouldBeLessThan total
    }

    "fork-join renderer stops early when cancelled partway" {
        val (written, total) = cancellationStopsEarly(::ForkJoinRenderer)

        written shouldBeLessThan total
    }

    "parallel renderer stops early when cancelled partway" {
        val (written, total) = cancellationStopsEarly(::ParallelRenderer)

        written shouldBeLessThan total
    }

    "coroutine block renderer stops early when cancelled partway" {
        val (written, total) = cancellationStopsEarly(::CoroutineBlockRenderer)

        written shouldBeLessThan total
    }

    "naive coroutine renderer stops early when cancelled partway" {
        val (written, total) = cancellationStopsEarly(::NaiveCoroutineRenderer)

        written shouldBeLessThan total
    }

    "virtual thread renderer stops early when cancelled partway" {
        val (written, total) = cancellationStopsEarly(::VirtualThreadBlockRenderer)

        written shouldBeLessThan total
    }
})
