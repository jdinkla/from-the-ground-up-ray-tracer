package net.dinkla.raytracer.btdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

/**
 * Pins the unsupported-operation contract of [PerfectTransmitter]: `f` and `rho` are not meaningful
 * for a perfect transmitter and reject the call with [UnsupportedOperationException] (formerly a bare
 * RuntimeException; the throw trigger is unchanged).
 */
internal class PerfectTransmitterUnsupportedOperationTest :
    StringSpec({

        "PerfectTransmitter.f is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    PerfectTransmitter().f(Shade(), Vector3D.ZERO, Vector3D.ZERO)
                }
            ex.message shouldContain "PerfectTransmitter"
        }

        "PerfectTransmitter.rho is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    PerfectTransmitter().rho(Shade(), Vector3D.ZERO)
                }
            ex.message shouldContain "PerfectTransmitter"
        }
    })
