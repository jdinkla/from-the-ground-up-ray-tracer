package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Element3DTest {

    private val x = 1.0
    private val y = 2.0
    private val z = 3.0
    private val e = Element3D(x, y, z)
    private val sqrLength = x * x + y * y + z * z
    private val length = Math.sqrt(sqrLength)

    @Test
    fun `construct from integers`() {
        assertEquals(e, Element3D(1.0, 2.0, 3.0))
    }

    @Test
    fun `construct from Element3D`() {
        assertEquals(e, Element3D(e))
    }

    @Test
    fun getX() {
        assertEquals(x, e.x)
    }

    @Test
    fun getY() {
        assertEquals(y, e.y)
    }

    @Test
    fun getZ() {
        assertEquals(z, e.z)
    }

    @Test
    fun sqrLength() {
        assertEquals(sqrLength, e.sqrLength())
    }

    @Test
    fun length() {
        assertEquals(length, e.length())
    }

    @Test
    fun sqrDistance() {
        val p = Element3D(0.0, 1.0, 2.0)
        assertEquals(1.0 + 1.0 + 1.0, e.sqrDistance(p))
    }

    @Test
    fun ith() {
        assertEquals(x, e.ith(Axis.X))
        assertEquals(y, e.ith(Axis.Y))
        assertEquals(z, e.ith(Axis.Z))
    }

    @Test
    fun equals() {
        assertTrue(e == Element3D(x, y, z))
        assertTrue(e != Element3D(0.0, y, z))
        assertTrue(e != Element3D(x, 0.0, z))
        assertTrue(e != Element3D(x, y, 0.0))
        assertTrue(e != Element2D(x, y))
    }
}