package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class NormalTest {

    private val x = 1.0
    private val y = 2.0
    private val z = 3.0
    private val d = 2.0

    private val n = Normal(x, y, z)
    private val e = n as Element3D
    private val v = Vector3D(x, y, z)

    @Test
    fun `construct from integers`() {
        assertEquals(e, Normal(1, 2, 3))
    }

    @Test
    fun `construct from numbers`() {
        assertEquals(x, n.x)
        assertEquals(y, n.y)
        assertEquals(z, n.z)
    }

    @Test
    fun `construct from vector`() {
        val n = Normal(v)
        val l = v.length()
        assertEquals(x/l, n.x)
        assertEquals(y/l, n.y)
        assertEquals(z/l, n.z)
    }

    @Test
    fun `construct from three points`() {
        val p0 = Point3D(x, y, z)
        val p1 = Point3D(y, z, x)
        val p2 = Point3D(z, x, y)
        val n = Normal(p0, p1, p2)
        val n2 = Normal(((p1 - p0) cross (p2 - p0)).normalize())
        assertEquals(n2, n)
    }

    @Test
    fun plus() {
        assertEquals(Vector3D(x+x, y+y, z+z), n + n)
    }

    @Test
    fun times() {
        assertEquals(Vector3D(d*x, d*y, d*z), n * d)
    }

    @Test
    fun dot() {
        assertEquals(x*x + y*y + z*z, n dot v)
    }

    @Test
    fun normalize() {
        val l = n.length()
        assertEquals(Normal(x/l, y/l, z/l), n.normalize())
    }

    @Test
    fun negate() {
        assertEquals(Normal(-x, -y, -z), -n)
    }
}