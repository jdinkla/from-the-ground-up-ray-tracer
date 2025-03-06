package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.MathUtils.max
import net.dinkla.raytracer.math.MathUtils.maxMax
import net.dinkla.raytracer.math.MathUtils.min

internal class MathUtilsTest :
    StringSpec({

        "min for 3 doubles" {
            min(1.0, 2.0, 3.0) shouldBe 1.0
            min(2.0, 1.0, 3.0) shouldBe 1.0
            min(1.0, 3.0, 2.0) shouldBe 1.0
            min(2.0, 3.0, 1.0) shouldBe 1.0
            min(3.0, 1.0, 2.0) shouldBe 1.0
            min(3.0, 2.0, 1.0) shouldBe 1.0
        }

        "max for 3 doubles" {
            max(1.0, 2.0, 3.0) shouldBe 3.0
            max(1.0, 3.0, 2.0) shouldBe 3.0
            max(2.0, 1.0, 3.0) shouldBe 3.0
            max(2.0, 3.0, 1.0) shouldBe 3.0
            max(3.0, 1.0, 2.0) shouldBe 3.0
            max(3.0, 2.0, 1.0) shouldBe 3.0
        }

        "maxMax should max the max" {
            maxMax(Point3D.X, 2.0 * Point3D.Y, 3.0 * Point3D.Z) shouldBe Point3D(1.0, 2.0, 3.0)
        }
    })
