package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class Point3DTest : AnnotationSpec() {

    private val p = Point3D(2.0, 3.0, 5.0)
    private val v = Vector3D(-2.0, -3.0, -5.0)
    private val e = Element3D(2.0, 3.0, 5.0)

    @Test
    fun `construct from integers`() {
        e shouldBe p
        p shouldNotBe e
    }

    @Test
    fun `construct from Element3D`() {
        e shouldBe Point3D(e)
    }

    @Test
    fun `add a vector`() {
        p + v shouldBe Point3D.ORIGIN
    }

    @Test
    fun `add a scalar`() {
        p + 2.0 shouldBe Point3D(4.0, 5.0, 7.0)
    }

    @Test
    fun `subtract a vector`() {
        p - v shouldBe Point3D(4.0, 6.0, 10.0)
    }

    @Test
    fun `subtract a point`() {
        p - p shouldBe Vector3D.ZERO
    }

    @Test
    fun `subtract a scalar`() {
        p - 2.0 shouldBe Point3D(0.0, 1.0, 3.0)
    }

    @Test
    fun `construct from element`() {
        Point3D(Element3D(2.0, 3.0, 5.0)) shouldBe p
    }

    @Test
    fun `points with same values are equal`() {
        p shouldBe Point3D(p.x, p.y, p.z)
    }

    @Test
    fun `points with different values are not equal`() {
        p shouldNotBe Point3D(0.0, p.y, p.z)
        p shouldNotBe Point3D(p.x, 0.0, p.z)
        p shouldNotBe Point3D(p.x, p.y, 0.0)
    }

    @Test
    fun equals() {
        val x = e.x
        val y = e.y
        val z = e.z
        e shouldBe Point3D(x, y, z)
        e shouldNotBe Point3D(0.0, y, z)
        e shouldNotBe Point3D(x, 0.0, z)
        e shouldNotBe Point3D(x, y, 0.0)
    }

    @Test
    fun `a point is not equal to a vector`() {
        p shouldNotBe Vector3D(p.x, p.y, p.z)
    }

    @Test
    fun unaryMinus() {
        val p = Point3D(v.x, v.y, -v.z)
        -p shouldBe Point3D(-v.x, -v.y, v.z)
    }
}