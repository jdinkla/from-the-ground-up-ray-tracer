package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class RayTest :
    StringSpec({

        val origin = Point3D(1.0, 1.0, 1.0)
        val direction = Vector3D.UP
        val ray = Ray(origin, direction)

        "construct from ray" {
            val copy = Ray(ray)
            copy.origin shouldBe origin
            copy.direction shouldBe direction
        }

        "linear" {
            ray.linear(0.5) shouldBe origin + (direction * 0.5)
        }
    })
