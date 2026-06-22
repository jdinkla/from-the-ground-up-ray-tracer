package net.dinkla.raytracer.brdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

/**
 * Pins the unsupported-operation contract of the BRDFs: certain interface methods are not meaningful
 * for some BRDFs and must reject the call. They throw [UnsupportedOperationException] with a
 * descriptive message (formerly a bare RuntimeException; the throw trigger is unchanged).
 */
internal class BrdfUnsupportedOperationTest :
    StringSpec({

        "Lambertian.sampleF is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    Lambertian().sampleF(Shade(), Vector3D.ZERO)
                }
            ex.message shouldContain "Lambertian"
        }

        "GlossySpecular.rho is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    GlossySpecular().rho(Shade(), Vector3D.ZERO)
                }
            ex.message shouldContain "GlossySpecular"
        }

        "PerfectSpecular.f is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    PerfectSpecular().f(Shade(), Vector3D.ZERO, Vector3D.ZERO)
                }
            ex.message shouldContain "PerfectSpecular"
        }

        "PerfectSpecular.rho is not supported" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    PerfectSpecular().rho(Shade(), Vector3D.ZERO)
                }
            ex.message shouldContain "PerfectSpecular"
        }
    })
