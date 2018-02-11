package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Point3DTest {

    private val p = Point3D(2.0, 3.0, 5.0)
    private val v = Vector3D(-2.0, -3.0, -5.0)

    @Test
    fun negate() {
        assertEquals(v, p.negate())
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
        assertEquals(Point3D.ORIGIN, p - p)
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

}