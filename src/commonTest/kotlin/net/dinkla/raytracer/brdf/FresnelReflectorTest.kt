package net.dinkla.raytracer.brdf

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox
import kotlin.math.cos
import kotlin.math.sin

/**
 * Fresnel reflectance for a dielectric interface (Suffern ch. 28). Expected values are derived
 * independently from the Fresnel equations:
 *  - normal incidence reduces to `((eta-1)/(eta+1))^2` (= 0.04 for eta = 1.5);
 *  - reflectance rises monotonically toward 1.0 at grazing incidence;
 *  - going to a less-dense medium past the critical angle, reflectance is 1.0 (total internal
 *    reflection — no energy is transmitted).
 */
class FresnelReflectorTest :
    StringSpec({

        // A hit whose surface normal is +y and whose incoming ray arrives at angle [thetaDeg] from
        // that normal (0 = head-on). The ray travels downward into the surface.
        fun hitAtAngle(thetaDeg: Double): Shade {
            val theta = thetaDeg * PI / DEGREES_HALF_TURN
            val direction = Vector3D(sin(theta), -cos(theta), 0.0)
            return Shade().apply {
                normal = Normal.UP
                ray = Ray(Point3D.ORIGIN, direction)
            }
        }

        "reflectance at normal incidence equals ((eta-1)/(eta+1))^2 for eta = 1.5" {
            val reflector = FresnelReflector(iorIn = 1.5, iorOut = 1.0)

            val kr = reflector.fresnel(hitAtAngle(0.0))

            kr shouldBeApprox 0.04
        }

        "reflectance at normal incidence equals ((eta-1)/(eta+1))^2 for eta = 2.0" {
            val reflector = FresnelReflector(iorIn = 2.0, iorOut = 1.0)
            // ((2-1)/(2+1))^2 = (1/3)^2 = 1/9
            val expected = 1.0 / 9.0

            val kr = reflector.fresnel(hitAtAngle(0.0))

            kr shouldBeApprox expected
        }

        "reflectance approaches 1.0 at grazing incidence" {
            val reflector = FresnelReflector(iorIn = 1.5, iorOut = 1.0)

            val kr = reflector.fresnel(hitAtAngle(GRAZING_DEG))

            kr shouldBeGreaterThan NEAR_ONE
            kr shouldBeLessThan ONE_PLUS
        }

        "reflectance increases monotonically from normal toward grazing incidence" {
            val reflector = FresnelReflector(iorIn = 1.5, iorOut = 1.0)

            val krNormal = reflector.fresnel(hitAtAngle(0.0))
            val krMid = reflector.fresnel(hitAtAngle(MID_DEG))
            val krGrazing = reflector.fresnel(hitAtAngle(GRAZING_DEG))

            krMid shouldBeGreaterThan krNormal
            krGrazing shouldBeGreaterThan krMid
        }

        "reflectance is 1.0 under total internal reflection (glass to air, past the critical angle)" {
            // Ray inside the glass (iorIn = 1.5) heading up toward the air boundary at 60 degrees,
            // well past the critical angle (~41.8 degrees), so all energy reflects.
            val reflector = FresnelReflector(iorIn = 1.5, iorOut = 1.0)
            val theta = TIR_DEG * PI / DEGREES_HALF_TURN
            val insideRay =
                Shade().apply {
                    normal = Normal.UP
                    ray = Ray(Point3D.ORIGIN, Vector3D(sin(theta), cos(theta), 0.0))
                }

            val kr = reflector.fresnel(insideRay)

            kr shouldBeApprox 1.0
        }
    }) {
    private companion object {
        private const val PI = 3.141592653589793
        private const val DEGREES_HALF_TURN = 180.0
        private const val MID_DEG = 45.0
        private const val GRAZING_DEG = 89.99
        private const val TIR_DEG = 60.0
        private const val NEAR_ONE = 0.99
        private const val ONE_PLUS = 1.0001
    }
}
