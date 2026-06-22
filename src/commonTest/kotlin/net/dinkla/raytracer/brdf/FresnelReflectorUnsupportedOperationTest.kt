package net.dinkla.raytracer.brdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

/**
 * Pins the unsupported-operation contract of [FresnelReflector]: like [PerfectSpecular], `f` and
 * `rho` are not meaningful for a perfectly specular reflector and reject the call.
 */
class FresnelReflectorUnsupportedOperationTest :
    StringSpec({

        "FresnelReflector.f is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    FresnelReflector().f(Shade(), Vector3D.ZERO, Vector3D.ZERO)
                }
            ex.message shouldContain "FresnelReflector"
        }

        "FresnelReflector.rho is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    FresnelReflector().rho(Shade(), Vector3D.ZERO)
                }
            ex.message shouldContain "FresnelReflector"
        }
    })
