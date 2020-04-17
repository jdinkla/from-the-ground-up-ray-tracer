package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.sqrt

internal class Element2DTest {

    private val x = 1.0
    private val y = 2.0
    private val e = Element2D(x, y)
    private val sqrLength = x * x + y * y
    private val length = sqrt(sqrLength)

    @Test
    fun getX() {
        assertEquals(x, e.x)
    }

    @Test
    fun getY() {
        assertEquals(y, e.y)
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
    fun equals() {
        assertTrue(e == Element2D(x, y))
        assertTrue(e != Element2D(0.0, y))
        assertTrue(e != Element2D(x, 0.0))
        assertTrue(e != Element3D(x, y, 0.0))
    }

}