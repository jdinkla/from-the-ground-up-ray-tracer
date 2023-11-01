package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.sqrt

internal class Point2DTest : StringSpec({
    val p = Point2D(1.0, 2.0)

    "add a vector" {
        val v = Vector2D(-1.0, -2.0)
        p + v shouldBe Point2D.ORIGIN
    }

    "subtract a vector" {
        val v = Vector2D(1.0, 2.0)
        p - v shouldBe Point2D.ORIGIN
    }

    "equals" {
        p shouldBe Point2D(1.0, 2.0)
        p shouldNotBe Point2D(1.0, 1.98)
        p shouldNotBe Point2D(0.98, 2.0)
        p shouldNotBe 3.0
        p shouldNotBe null
    }

    "unaryMinus" {
        -p shouldBe Vector2D(-1.0, -2.0)
    }

    "length" {
        p.length shouldBe sqrt(1.0 + 4.0)
    }
})
