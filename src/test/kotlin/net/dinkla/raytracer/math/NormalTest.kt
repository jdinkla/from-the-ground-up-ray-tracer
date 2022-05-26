package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class NormalTest : AnnotationSpec()  {

    private val x = 1.0
    private val y = 2.0
    private val z = 3.0
    private val d = 2.0

    private val n = Normal(x, y, z)
    private val e = n as Element3D
    private val v = Vector3D(x, y, z)

    @Test
    fun `construct from integers`() {
        Normal(1.0, 2.0, 3.0) shouldBe e
    }

    @Test
    fun `construct from numbers`() {
        n.x shouldBe x
        n.y shouldBe y
        n.z shouldBe z
    }

    @Test
    fun `construct from vector`() {
        val n = Normal(v)
        val l = v.length()
        n.x shouldBe x/l
        n.y shouldBe y/l
        n.z shouldBe z/l
    }

    @Test
    fun `construct from three points`() {
        val p0 = Point3D(x, y, z)
        val p1 = Point3D(y, z, x)
        val p2 = Point3D(z, x, y)
        val n = Normal(p0, p1, p2)
        val n2 = Normal(((p1 - p0) cross (p2 - p0)).normalize())
        n shouldBe n2
    }

    @Test
    fun plus() {
        n + n shouldBe Vector3D(x+x, y+y, z+z)
    }

    @Test
    fun times() {
        n * d shouldBe Vector3D(d*x, d*y, d*z)
    }

    @Test
    fun dot() {
        n dot v shouldBe x*x + y*y + z*z
    }

    @Test
    fun normalize() {
        val l = n.length()
        n.normalize() shouldBe Normal(x/l, y/l, z/l)
    }

    @Test
    fun negate() {
        -n shouldBe Normal(-x, -y, -z)
    }
}