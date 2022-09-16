package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.PointUtilities.maximum
import net.dinkla.raytracer.math.PointUtilities.minimum

internal class PointUtilitiesTest : StringSpec({

    val p0 = Point3D.ORIGIN
    val p1 = Point3D.UNIT
    val a = arrayOf(p0, p1)

    "should return the minimum of an array" {
        val (x, y, z) = minimum(a, 2)
        x shouldBe 0.0
        y shouldBe 0.0
        z shouldBe 0.0
    }

    "should return the maximum§ of an array" {
        val (x, y, z) = maximum(a, 2)
        x shouldBe 1.0
        y shouldBe 1.0
        z shouldBe 1.0
    }
})