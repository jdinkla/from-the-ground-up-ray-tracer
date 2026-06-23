package net.dinkla.raytracer

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

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

        "applyResolution lowering the resolution scales the pixel size up, preserving the field of view" {
            // Default view plane is 1080p at sizeOfPixel 1.0, so the world-space height extent is 1080.
            val vp = ViewPlane()
            val extentBefore = vp.sizeOfPixel * vp.resolution.height

            vp.applyResolution(Resolution(720))

            vp.resolution shouldBe Resolution(720)
            vp.sizeOfPixel shouldBeApprox 1.5 // 1.0 * 1080/720 keeps sizeOfPixel*height constant
            (vp.sizeOfPixel * vp.resolution.height) shouldBeApprox extentBefore
        }

        "applyResolution raising the resolution scales the pixel size down, preserving the field of view" {
            val vp = ViewPlane()
            val extentBefore = vp.sizeOfPixel * vp.resolution.height

            vp.applyResolution(Resolution(2160))

            vp.sizeOfPixel shouldBeApprox 0.5 // 1.0 * 1080/2160
            (vp.sizeOfPixel * vp.resolution.height) shouldBeApprox extentBefore
        }

        "applyResolution to the reference 1080p resolution leaves the pixel size unchanged" {
            // Guards AC#4: at the default reference resolution the rescale is a no-op, so 1080p
            // renders stay byte-identical to before TASK-36.
            val vp = ViewPlane()

            vp.applyResolution(Resolution(1080))

            vp.resolution shouldBe Resolution(1080)
            vp.sizeOfPixel shouldBeApprox 1.0
        }

        "the view-plane extent (field of view) is the same at every predefined resolution" {
            val referenceExtent = ViewPlane().let { it.sizeOfPixel * it.resolution.height } // 1080.0

            Resolution.resolutions.forEach { predefined ->
                val vp = ViewPlane()

                vp.applyResolution(predefined.create())

                (vp.sizeOfPixel * vp.resolution.height) shouldBeApprox referenceExtent
            }
        }
    })
