package net.dinkla.raytracer.world

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.examples.YellowAndRedSphere
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.renderer.CancellationToken
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Resolution
import kotlin.time.Duration

/**
 * Pins the metric contract of [Render]: render time and the [Counter] tallies are returned as
 * structured [RenderStats]/[RenderResult] values (not logged at the measurement site), and the
 * per-render counter reset that the pipeline has always relied on is preserved.
 */
class RenderStatsTest :
    StringSpec({

        // Counter is a process-wide singleton; clear it before each scenario so tallies from other
        // tests cannot leak into the assertions.
        beforeTest {
            Counter.reset()
        }

        // A throwaway 1x1 film: the fake renderer below never writes to it, it only satisfies the type.
        val fakeFilm =
            object : IFilm {
                override val resolution = Resolution(1, 1)

                override fun setPixel(
                    x: Int,
                    y: Int,
                    color: Color,
                ) = Unit
            }

        "render(film, renderer) returns the elapsed duration and a snapshot of the counters" {
            val renderer =
                object : IRenderer {
                    override fun render(
                        film: IFilm,
                        cancellation: CancellationToken,
                    ) {
                        Counter.count("RenderTest.event")
                        Counter.count("RenderTest.event")
                    }
                }

            val stats = Render.render(fakeFilm, renderer)

            stats.duration shouldBeGreaterThanOrEqualTo Duration.ZERO
            stats.counts["RenderTest.event"] shouldBe 2
        }

        "render(film, renderer) resets the counters after snapshotting them" {
            val renderer =
                object : IRenderer {
                    override fun render(
                        film: IFilm,
                        cancellation: CancellationToken,
                    ) {
                        Counter.count("RenderTest.event")
                    }
                }

            Render.render(fakeFilm, renderer)

            // The snapshot is captured into the stats, then the global counter is cleared for the
            // next render — so the key no longer survives in the live counter.
            Counter.snapshot()["RenderTest.event"].shouldBeNull()
        }

        "render(worldDefinition, context) returns the film, the world and the render stats" {
            val resolution = Resolution(8, 8)
            val context = Context(Tracers.WHITTED.create, Renderer.SEQUENTIAL.creator, resolution)

            val result = Render.render(YellowAndRedSphere, context)

            result.film.resolution shouldBe resolution
            result.stats.duration shouldBeGreaterThanOrEqualTo Duration.ZERO
        }
    })
