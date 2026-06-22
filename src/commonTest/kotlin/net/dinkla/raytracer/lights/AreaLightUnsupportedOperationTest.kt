package net.dinkla.raytracer.lights

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Point3D

/**
 * Pins the unsupported-operation contract of [AreaLight]: the bare [ILightSource]/[Light] methods are
 * only valid through the AreaLighting tracer and reject direct calls with
 * [UnsupportedOperationException] (formerly a bare RuntimeException; the throw trigger is unchanged).
 */
internal class AreaLightUnsupportedOperationTest :
    StringSpec({

        "AreaLight.sample requires the AreaLighting tracer" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    AreaLight().sample()
                }
            ex.message shouldContain "AreaLighting"
        }

        "AreaLight.getNormal requires the AreaLighting tracer" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    AreaLight().getNormal(Point3D.ORIGIN)
                }
            ex.message shouldContain "AreaLighting"
        }

        "AreaLight.getDirection requires the AreaLighting tracer" {
            val ex =
                shouldThrow<UnsupportedOperationException> {
                    AreaLight().getDirection(Shade())
                }
            ex.message shouldContain "AreaLighting"
        }
    })
