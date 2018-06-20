package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Point3DTest {

    private val p = Point3D(2.0, 3.0, 5.0)
    private val v = Vector3D(-2.0, -3.0, -5.0)
    private val e = Element3D(2.0, 3.0, 5.0)

    @Test
    fun `construct from integers`() {
        assertEquals(e, Point3D(2, 3, 5))
    }

    @Test
    fun `construct from Element3D`() {
        assertEquals(e, Point3D(e))
    }

    @Test
    fun `add a vector`() {
        assertEquals(Point3D.ORIGIN, p + v)
    }

    @Test
    fun `add a scalar`() {
        assertEquals(Point3D(4.0, 5.0, 7.0), p + 2.0)
    }

    @Test
    fun `subtract a vector`() {
        assertEquals(Point3D(4.0, 6.0, 10.0), p - v)
    }

    @Test
    fun `subtract a point`() {
        assertEquals(Vector3D.ZERO, p - p)
    }

    @Test
    fun `subtract a scalar`() {
        assertEquals(Point3D(0.0, 1.0, 3.0), p - 2.0)
    }

    @Test
    fun `construct from element`() {
        val e = Element3D(2.0, 3.0, 5.0)
        val q = Point3D(e)
        assertEquals(p, q)
    }

    @Test
    fun `points with same values are equal`() {
        assertEquals(p, Point3D(p.x, p.y, p.z))
    }

    @Test
    fun `points with different values are not equal`() {
        assertNotEquals(p, Point3D(0.0, p.y, p.z))
        assertNotEquals(p, Point3D(p.x, 0.0, p.z))
        assertNotEquals(p, Point3D(p.x, p.y, 0.0))
    }

    @Test
    fun `a point is not equal to a vector`() {
        assertNotEquals(p, Vector3D(p.x, p.y, p.z))
    }

    @Test
    fun unaryMinus() {
        val p = Point3D(v.x, v.y, -v.z)
        assertEquals(-p, Point3D(-v.x, -v.y, v.z))
    }
}