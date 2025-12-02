package net.dinkla.raytracer.renderer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution
import java.util.Collections

private class RecordingFilm(
    override val resolution: Resolution,
) : IFilm {
    val writes: MutableList<Pair<Int, Int>> = Collections.synchronizedList(mutableListOf())

    override fun setPixel(
        x: Int,
        y: Int,
        color: Color,
    ) {
        writes.add(x to y)
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

private object IdentityCorrector : IColorCorrector {
    override fun correct(color: Color): Color = color
}

class RendererTest : StringSpec({
    "parallel renderer fills all pixels" {
        val film = RecordingFilm(Resolution(width = 4, height = 4))
        val renderer = ParallelRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        renderer.render(film)

        film.writes.size shouldBe film.resolution.width * film.resolution.height
    }

    "parallel renderer fails on incompatible resolution" {
        val film = RecordingFilm(Resolution(width = 3, height = 5))
        val renderer = ParallelRenderer(StubSingleRayRenderer(Color.WHITE), IdentityCorrector)

        shouldThrow<RuntimeException> {
            renderer.render(film)
        }
    }
})
