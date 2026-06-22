package net.dinkla.raytracer.cameras

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution

/** A fake [IFilm] that records the last colour written to each pixel, for asserting placement. */
private class RecordingFilm(
    override val resolution: Resolution,
) : IFilm {
    val written = mutableMapOf<Pair<Int, Int>, Color>()

    override fun setPixel(
        x: Int,
        y: Int,
        color: Color,
    ) {
        written[x to y] = color
    }
}

class StereoCompositorTest :
    StringSpec({
        // A 2x2 left image and a 2x2 right image with distinct, easily traced colours per pixel.
        val left =
            mapOf(
                (0 to 0) to Color(0.1, 0.2, 0.3),
                (1 to 0) to Color(0.4, 0.5, 0.6),
                (0 to 1) to Color(0.7, 0.8, 0.9),
                (1 to 1) to Color(0.15, 0.25, 0.35),
            )
        val right =
            mapOf(
                (0 to 0) to Color(0.9, 0.8, 0.7),
                (1 to 0) to Color(0.6, 0.5, 0.4),
                (0 to 1) to Color(0.3, 0.2, 0.1),
                (1 to 1) to Color(0.55, 0.45, 0.35),
            )
        val leftSource = StereoCompositor.PixelSource { x, y -> left.getValue(x to y) }
        val rightSource = StereoCompositor.PixelSource { x, y -> right.getValue(x to y) }

        "side-by-side places the left image in columns [0, width) of a double-width film" {
            val out = RecordingFilm(Resolution(4, 2))

            StereoCompositor.sideBySide(leftSource, rightSource, width = 2, height = 2, out = out)

            out.written[0 to 0] shouldBe Color(0.1, 0.2, 0.3)
            out.written[1 to 0] shouldBe Color(0.4, 0.5, 0.6)
            out.written[0 to 1] shouldBe Color(0.7, 0.8, 0.9)
            out.written[1 to 1] shouldBe Color(0.15, 0.25, 0.35)
        }

        "side-by-side places the right image in columns [width, 2*width) of a double-width film" {
            val out = RecordingFilm(Resolution(4, 2))

            StereoCompositor.sideBySide(leftSource, rightSource, width = 2, height = 2, out = out)

            out.written[2 to 0] shouldBe Color(0.9, 0.8, 0.7)
            out.written[3 to 0] shouldBe Color(0.6, 0.5, 0.4)
            out.written[2 to 1] shouldBe Color(0.3, 0.2, 0.1)
            out.written[3 to 1] shouldBe Color(0.55, 0.45, 0.35)
        }

        "side-by-side writes exactly 2*width*height pixels" {
            val out = RecordingFilm(Resolution(4, 2))

            StereoCompositor.sideBySide(leftSource, rightSource, width = 2, height = 2, out = out)

            out.written.size shouldBe 8
        }

        "anaglyph takes red from the left eye and green/blue from the right eye" {
            val out = RecordingFilm(Resolution(2, 2))

            StereoCompositor.anaglyph(leftSource, rightSource, width = 2, height = 2, out = out)

            // pixel (0,0): left red 0.1, right green 0.8, right blue 0.7
            out.written[0 to 0] shouldBe Color(0.1, 0.8, 0.7)
            // pixel (1,0): left red 0.4, right green 0.5, right blue 0.4
            out.written[1 to 0] shouldBe Color(0.4, 0.5, 0.4)
            // pixel (0,1): left red 0.7, right green 0.2, right blue 0.1
            out.written[0 to 1] shouldBe Color(0.7, 0.2, 0.1)
            // pixel (1,1): left red 0.15, right green 0.45, right blue 0.35
            out.written[1 to 1] shouldBe Color(0.15, 0.45, 0.35)
        }

        "anaglyph writes exactly width*height pixels (single-eye width)" {
            val out = RecordingFilm(Resolution(2, 2))

            StereoCompositor.anaglyph(leftSource, rightSource, width = 2, height = 2, out = out)

            out.written.size shouldBe 4
        }
    })
