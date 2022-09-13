package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.sqrt

internal class Element2DTest : StringSpec({
    val x = 1.0
    val y = 2.0
    val e = Element2D(x, y)
    val sqrLength = x * x + y * y

    "getX" {
        e.x shouldBe x
    }

    "getY" {
        e.y shouldBe y
    }

    "sqrLength" {
        e.sqrLength() shouldBe sqrLength
    }

    "length" {
        e.length() shouldBe sqrt(sqrLength)
    }

    "equals" {
        Element2D(x, y) shouldBe e
        Element2D(0.0, y) shouldNotBe e
        Element2D(x, 0.0) shouldNotBe e
        Element3D(x, y, 0.0) shouldNotBe e
    }
})