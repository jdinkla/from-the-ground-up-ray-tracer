package net.dinkla.raytracer.btdf

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.cos
import kotlin.math.sin

/**
 * Covers [PerfectTransmitter]'s direction math and total-internal-reflection test (Suffern ch. 27):
 *
 *  - [sampleF] orients the interface by the sign of `n·wo`: when the viewer is on the front side
 *    (`n·wo > 0`) the normal is used as-is; when it is behind (`n·wo < 0`) the normal and the
 *    relative index are flipped. Both branches must produce a transmitted ray that continues
 *    *through* the surface (here, downward through an up-facing surface).
 *  - [isTir] is true only when the radicand `cosThetaTSqr` goes negative — a ray leaving a denser
 *    medium past the critical angle — and false otherwise.
 *
 * `f` and `rho` (unsupported) are pinned by [PerfectTransmitterUnsupportedOperationTest].
 */
internal class PerfectTransmitterTest :
    StringSpec({

        // A hit on an up-facing surface; the ray direction drives isTir, the wo argument drives sampleF.
        fun hitWithRayDirection(direction: Vector3D): Shade =
            Shade().apply {
                normal = Normal.UP
                ray = Ray(Point3D.ORIGIN, direction)
            }

        "sampleF transmits straight through when the viewer is on the front side" {
            val transmitter = PerfectTransmitter(ior = 2.0)
            val sr = hitWithRayDirection(Vector3D(0.0, -1.0, 0.0))
            val wo = Vector3D(0.0, 1.0, 0.0) // n·wo = 1 > 0, no flip

            val wt = transmitter.sampleF(sr, wo).wt

            // normal incidence => the transmitted ray continues straight down through the surface.
            wt.y shouldBeLessThan 0.0
        }

        "sampleF flips the interface and still transmits through when the viewer is behind the surface" {
            val transmitter = PerfectTransmitter(ior = 2.0)
            val sr = hitWithRayDirection(Vector3D(0.0, 1.0, 0.0))
            val wo = Vector3D(0.0, -1.0, 0.0) // n·wo = -1 < 0, flip branch

            val wt = transmitter.sampleF(sr, wo).wt

            // After flipping the (now downward) normal the ray still exits downward through the surface.
            wt.y shouldBeLessThan 0.0
        }

        "total internal reflection when a ray leaves a denser medium past the critical angle" {
            val transmitter = PerfectTransmitter(ior = 1.5)
            // ray pointing up-and-out at 60 degrees from the normal: n·wo < 0 (exiting), grazing enough to TIR.
            val theta = ABOVE_CRITICAL_DEG * PI / DEGREES_HALF_TURN
            val sr = hitWithRayDirection(Vector3D(sin(theta), cos(theta), 0.0))

            transmitter.isTir(sr) shouldBe true
        }

        "no total internal reflection for a steeper exit angle inside the critical cone" {
            val transmitter = PerfectTransmitter(ior = 1.5)
            // ray exiting closer to the normal (20 degrees): the radicand stays non-negative.
            val theta = BELOW_CRITICAL_DEG * PI / DEGREES_HALF_TURN
            val sr = hitWithRayDirection(Vector3D(sin(theta), cos(theta), 0.0))

            transmitter.isTir(sr) shouldBe false
        }

        "no total internal reflection when entering the medium (viewer on the front side)" {
            val transmitter = PerfectTransmitter(ior = 1.5)
            // ray coming down onto the surface: n·wo > 0, eta = ior > 1, always transmits.
            val sr = hitWithRayDirection(Vector3D(0.0, -1.0, 0.0))

            transmitter.isTir(sr) shouldBe false
        }
    }) {
    private companion object {
        private const val PI = 3.141592653589793
        private const val DEGREES_HALF_TURN = 180.0
        private const val ABOVE_CRITICAL_DEG = 60.0
        private const val BELOW_CRITICAL_DEG = 20.0
    }
}
