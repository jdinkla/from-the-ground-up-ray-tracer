package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class Point3DTest : AnnotationSpec() {

    private val p = Point3D(2.0, 3.0, 5.0)
    private val v = Vector3D(-2.0, -3.0, -5.0)

    private val x = 1.0
    private val y = 2.0
    private val z = 3.0
    private val e = Point3D(x, y, z)

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
        val x = p.x
        val y = p.y
        val z = p.z
        p shouldBe Point3D(x, y, z)
        p shouldNotBe Point3D(0.0, y, z)
        p shouldNotBe Point3D(x, 0.0, z)
        p shouldNotBe Point3D(x, y, 0.0)
    }

    @Test
    fun `a point is not equal to a vector`() {
        p shouldNotBe Vector3D(p.x, p.y, p.z)
    }

    @Test
    fun unaryMinus() {
        val p = Point3D(v.x, v.y, -v.z)
        -p shouldBe Vector3D(-v.x, -v.y, v.z)
    }

    @Test
    fun sqrDistance() {
        val p = Point3D(0.0, 1.0, 2.0)
        e.sqrDistance(p) shouldBe 1.0 + 1.0 + 1.0
    }

    @Test
    fun ith() {
        e.ith(Axis.X) shouldBe x
        e.ith(Axis.Y) shouldBe y
        e.ith(Axis.Z) shouldBe z
    }

    @Test
    fun times() {
        2.0 * e shouldBe Point3D(2.0 * e.x, 2.0 * e.y, 2.0 * e.z)
    }
}