package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class Point2DTest : AnnotationSpec() {

    private val p = Point2D(1.0, 2.0)

    @Test
    fun `add a vector`() {
        val v = Vector2D(-1.0, -2.0)
        p + v shouldBe Point2D.ORIGIN
    }

    @Test
    fun `subtract a vector`() {
        val v = Vector2D(1.0, 2.0)
        p - v shouldBe Point2D.ORIGIN
    }

    @Test
    fun equals() {
        p shouldBe Point2D(1.0, 2.0)
        p shouldNotBe Point2D(1.0, 1.98)
        p shouldNotBe Point2D(0.98, 2.0)
        p shouldNotBe 3.0
        p shouldNotBe null
    }

    @Test
    fun unaryMinus() {
        -p shouldBe Point2D(-1.0, -2.0)
    }
}