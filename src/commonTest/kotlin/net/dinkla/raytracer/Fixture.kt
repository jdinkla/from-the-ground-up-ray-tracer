package net.dinkla.raytracer

import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Point3D

infix fun Double.shouldBeApprox(expected: Double) {
    this shouldBeGreaterThanOrEqual expected - MathUtils.K_EPSILON
    this shouldBeLessThanOrEqual expected + MathUtils.K_EPSILON
}

infix fun Point3D.shouldBeApprox(expected: Point3D) {
    this.x shouldBeApprox expected.x
    this.y shouldBeApprox expected.y
    this.z shouldBeApprox expected.z
}

object Fixture {

    object ex {
        const val ka = 0.1
        const val kd = 0.2
        const val ks = 0.3
        const val kr = 0.4
        const val exp = 0.5
        const val kt = 0.98
        const val ior = 0.07
        val cd = Color(1.0, 0.9, 0.8)
        val cr = Color(0.1, 0.2, 0.3)
        val cs = Color(0.4, 0.5, 0.6)
    }
}
