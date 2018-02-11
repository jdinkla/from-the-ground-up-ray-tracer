package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Vector2DTest {

    private val v0 = Vector2D(0.0, 0.0)
    private val v = Vector2D(2.0, 3.0)
    private val w = Vector2D(-2.0, -3.0)

    private val a = 3.0
    private val b = 5.0
    private val c = 7.0
    private val d = 11.0

    @Test
    fun `add vector`() {
        assertEquals(v0, v + w)
    }

    @Test
    fun `subtract vector`() {
        assertEquals(v0, v - v)
    }

    @Test
    fun `multiply with scalar`() {
        val s = 2.0
        assertEquals(Vector2D(v.x * s, v.y * s), v * s)
    }

    @Test
    fun `dot product with vector`() {
        assertEquals(a*c + b*d, Vector2D(a, b) dot Vector2D(c, d))
    }

    @Test
    fun `dot product with normal`() {
        assertEquals(a*c + b*d, Vector2D(a, b) dot Normal(c, d, 0.0))
    }

    @Test
    fun normalize() {
        val x = v.x / v.length()
        val y = v.y / v.length()
        assertEquals(Vector2D(x, y), v.normalize())
    }
}