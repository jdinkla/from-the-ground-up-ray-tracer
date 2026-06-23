package net.dinkla.raytracer.btdf

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.brdf.FresnelReflector
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox
import kotlin.math.cos
import kotlin.math.sin

/**
 * Fresnel transmittance and total-internal-reflection threshold for a dielectric interface
 * (Suffern ch. 28). The critical angle for glass (1.5) to air (1.0) is
 * `asin(1/1.5) ~= 41.8 degrees`; below it a ray transmits, above it all energy reflects.
 */
class FresnelTransmitterTest :
    StringSpec({

        // A hit on a +y-normal surface with a ray inside the medium heading up-and-out at
        // [thetaDeg] from the normal (the geometry that produces TIR past the critical angle).
        fun insideRayAtAngle(thetaDeg: Double): Shade {
            val theta = thetaDeg * PI / DEGREES_HALF_TURN
            return Shade().apply {
                normal = Normal.UP
                ray = Ray(Point3D.ORIGIN, Vector3D(sin(theta), cos(theta), 0.0))
            }
        }

        "no total internal reflection just inside the critical angle" {
            val transmitter = FresnelTransmitter(iorIn = 1.5, iorOut = 1.0)

            transmitter.isTir(insideRayAtAngle(BELOW_CRITICAL_DEG)) shouldBe false
        }

        "total internal reflection just past the critical angle" {
            val transmitter = FresnelTransmitter(iorIn = 1.5, iorOut = 1.0)

            transmitter.isTir(insideRayAtAngle(ABOVE_CRITICAL_DEG)) shouldBe true
        }

        "no total internal reflection when entering a denser medium at any angle" {
            // Air (1.0) to glass (1.5): the relative index is > 1, so a transmitted ray always exists.
            val transmitter = FresnelTransmitter(iorIn = 1.5, iorOut = 1.0)
            val enteringRay =
                Shade().apply {
                    normal = Normal.UP
                    // ray coming down from the air side at a steep angle
                    val theta = STEEP_DEG * PI / DEGREES_HALF_TURN
                    ray = Ray(Point3D.ORIGIN, Vector3D(sin(theta), -cos(theta), 0.0))
                }

            transmitter.isTir(enteringRay) shouldBe false
        }

        "transmitted direction bends toward the normal when entering a denser medium" {
            // Entering glass from air: the refracted ray should be steeper (closer to -y) than the
            // incident ray, i.e. its x-component shrinks relative to the (downward) incident ray.
            val transmitter = FresnelTransmitter(iorIn = 1.5, iorOut = 1.0)
            val theta = STEEP_DEG * PI / DEGREES_HALF_TURN
            val incident = Vector3D(sin(theta), -cos(theta), 0.0)
            val sr =
                Shade().apply {
                    normal = Normal.UP
                    ray = Ray(Point3D.ORIGIN, incident)
                }

            val wt = transmitter.sampleF(sr, -incident).wt

            // refracted ray still travels downward into the glass
            (wt.y < 0.0) shouldBe true
            // and bends toward the normal: smaller horizontal deflection than the incident ray
            (kotlin.math.abs(wt.normalize().x) < kotlin.math.abs(incident.x)) shouldBe true
        }

        "transmitting a ray leaving the medium below the critical angle bends it away from the normal" {
            // Exit branch of sampleF: a ray inside the glass heading up-and-out at 30 degrees (below the
            // ~41.8 degree critical angle, so a transmitted ray exists). Here wo = -ray.direction has a
            // negative dot with the +y normal, exercising the flip branch of sampleF and the
            // cosThetaI < 0 branch of the internal Fresnel computation.
            val transmitter = FresnelTransmitter(iorIn = 1.5, iorOut = 1.0)
            val theta = BELOW_CRITICAL_DEG * PI / DEGREES_HALF_TURN
            val incident = Vector3D(sin(theta), cos(theta), 0.0)
            val sr =
                Shade().apply {
                    normal = Normal.UP
                    ray = Ray(Point3D.ORIGIN, incident)
                }

            val wt = transmitter.sampleF(sr, -incident).wt

            // The refracted ray continues outward (still travels upward, away from the medium) and, going
            // from a denser to a thinner medium, bends away from the normal: larger horizontal deflection.
            (wt.y > 0.0) shouldBe true
            (kotlin.math.abs(wt.normalize().x) > kotlin.math.abs(incident.x)) shouldBe true
        }

        "the TIR threshold coincides with reflectance reaching 1.0" {
            // The critical angle ties the BTDF and BRDF together: just inside it some energy is
            // transmitted (reflectance < 1, no TIR); just past it all energy reflects (kr = 1, TIR).
            val reflector = FresnelReflector(iorIn = 1.5, iorOut = 1.0)
            val transmitter = FresnelTransmitter(iorIn = 1.5, iorOut = 1.0)

            val below = insideRayAtAngle(BELOW_CRITICAL_DEG)
            val above = insideRayAtAngle(ABOVE_CRITICAL_DEG)

            transmitter.isTir(below) shouldBe false
            (reflector.fresnel(below) < 1.0) shouldBe true
            transmitter.isTir(above) shouldBe true
            reflector.fresnel(above) shouldBeApprox 1.0
        }
    }) {
    private companion object {
        private const val PI = 3.141592653589793
        private const val DEGREES_HALF_TURN = 180.0
        private const val BELOW_CRITICAL_DEG = 30.0
        private const val ABOVE_CRITICAL_DEG = 60.0
        private const val STEEP_DEG = 60.0
    }
}
