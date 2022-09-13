package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.sqrt

internal class Element3DTest : AnnotationSpec() {

    private val x = 1.0
    private val y = 2.0
    private val z = 3.0
    private val e = Element3D(x, y, z)
    private val sqrLength = x * x + y * y + z * z
    private val length = sqrt(sqrLength)

    @Test
    fun `construct from integers`() {
        Element3D(1.0, 2.0, 3.0) shouldBe e
    }

    @Test
    fun `construct from Element3D`() {
        Element3D(e) shouldBe e
    }

    @Test
    fun getX() {
        e.x shouldBe x
    }

    @Test
    fun getY() {
        e.y shouldBe y
    }

    @Test
    fun getZ() {
        e.z shouldBe z
    }

    @Test
    fun sqrLength() {
        e.sqrLength() shouldBe sqrLength
    }

    @Test
    fun length() {
        e.length() shouldBe length
    }

    @Test
    fun sqrDistance() {
        val p = Element3D(0.0, 1.0, 2.0)
        e.sqrDistance(p) shouldBe 1.0 + 1.0 + 1.0
    }

    @Test
    fun ith() {
        e.ith(Axis.X) shouldBe x
        e.ith(Axis.Y) shouldBe y
        e.ith(Axis.Z) shouldBe z
    }

    @Test
    fun equals() {
        Element3D(x, y, z) shouldBe e
        Element3D(0.0, y, z) shouldNotBe e
        Element3D(x, 0.0, z) shouldNotBe e
        Element3D(x, y, 0.0) shouldNotBe e
    }
}