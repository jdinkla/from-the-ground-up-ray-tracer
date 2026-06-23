package net.dinkla.raytracer

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.colors.Color

/**
 * Tests [ViewPlane]'s default colour-correction contract. With the out-of-gamut debug flag off and
 * gamma at 1.0 (the defaults every scene uses, and the only externally reachable configuration),
 * `correct` is the `maxToOne` tone map: an in-gamut colour passes through unchanged, while a colour
 * whose brightest channel exceeds 1.0 is scaled down so that channel becomes exactly 1.0.
 */
internal class ViewPlaneTest :
    StringSpec({

        "passes an in-gamut colour through unchanged" {
            val vp = ViewPlane()
            val color = Color(0.2, 0.4, 0.6)

            vp.correct(color) shouldBeApprox color
        }

        "scales an over-bright colour so its largest channel becomes one" {
            val vp = ViewPlane()
            // Largest channel is 2.0; maxToOne divides by 2.0 => (0.5, 1.0, 0.25).
            val color = Color(1.0, 2.0, 0.5)

            vp.correct(color) shouldBeApprox Color(0.5, 1.0, 0.25)
        }

        "exposes the documented defaults for recursion depth and sample count" {
            val vp = ViewPlane()

            vp.maximalRecursionDepth shouldBe 5
            vp.numSamples shouldBe 1
            vp.sizeOfPixel shouldBeApprox 1.0
        }

        "toString reports the view-plane configuration" {
            val vp = ViewPlane()

            vp.toString() shouldContain "maxDepth=5"
        }
    })
