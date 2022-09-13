package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.sqrt

internal class Element2DTest : AnnotationSpec() {

    private val x = 1.0
    private val y = 2.0
    private val e = Element2D(x, y)
    private val sqrLength = x * x + y * y

    @Test
    fun getX() {
        e.x shouldBe x
    }

    @Test
    fun getY() {
        e.y shouldBe y
    }

    @Test
    fun sqrLength() {
        e.sqrLength() shouldBe sqrLength
    }

    @Test
    fun length() {
        e.length() shouldBe sqrt(sqrLength)
    }

    @Test
    fun equals() {
        Element2D(x, y) shouldBe e
        Element2D(0.0, y) shouldNotBe e
        Element2D(x, 0.0) shouldNotBe e
        Element3D(x, y, 0.0) shouldNotBe e
    }
}