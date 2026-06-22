package net.dinkla.raytracer.btdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

/**
 * Pins the unsupported-operation contract of [FresnelTransmitter]: like [PerfectTransmitter], `f`
 * and `rho` are not meaningful for a perfect transmitter and reject the call.
 */
class FresnelTransmitterUnsupportedOperationTest :
    StringSpec({

        "FresnelTransmitter.f is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    FresnelTransmitter().f(Shade(), Vector3D.ZERO, Vector3D.ZERO)
                }
            ex.message shouldContain "FresnelTransmitter"
        }

        "FresnelTransmitter.rho is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    FresnelTransmitter().rho(Shade(), Vector3D.ZERO)
                }
            ex.message shouldContain "FresnelTransmitter"
        }
    })
