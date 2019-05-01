package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.exp

internal class Vector3DTest {

    private val v0 = Vector3D(0.0, 0.0, 0.0)
    private val v = Vector3D(2.0, 3.0, 5.0)
    private val w = Vector3D(-2.0, -3.0, -5.0)
    private val el: Element3D = v

    private val a = 3.0
    private val b = 5.0
    private val c = 7.0
    private val d = 11.0
    private val e = 13.0
    private val f = 17.0

    private val v1 = Vector3D(a, b, c)
    private val v2 = Vector3D(d, e, f)

    @Test
    fun `construct from integers`() {
        assertEquals(v, Vector3D(2, 3, 5))
    }

    @Test
    fun `construct from Element3D`() {
        assertEquals(v, Vector3D(el))
    }

    @Test
    fun `add vector`() {
        assertEquals(v0, v + w)
    }

    @Test
    fun `subtract vector`() {
        assertEquals(v0, v - v)
    }

    @Test
    fun `right scalar multiplication`() {
        val s = 2.0
        assertEquals(Vector3D(v.x*s, v.y*s, v.z*s), v * s)
    }

    @Test
    fun `left scalar multiplication`() {
        val s = 2.0
        assertEquals(Vector3D(v.x*s, v.y*s, v.z*s), s * v)
    }

    @Test
    fun `dot product with vector`() {
        assertEquals(a*d + b*e + c*f, v1 dot v2)
    }

    @Test
    fun `dot product with normal`() {
        assertEquals(a*d + b*e + c*f, v1 dot Normal(d, e, f))
    }

    @Test
    fun `cross product`() {
        val expected = Vector3D(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x)
        assertEquals(expected, v1 cross v2)
    }

    @Test
    fun normalize() {
        val x = v.x / v.length()
        val y = v.y / v.length()
        val z = v.z / v.length()
        assertEquals(Vector3D(x, y, z), v.normalize())
    }

    @Test
    fun negate() {
        assertEquals(v, -w)
    }

    @Test
    fun volume() {
        assertEquals(a*b*c, v1.volume())
    }

    @Test
    fun unaryMinus() {
        val v = Vector3D(a, b, -c)
        assertEquals(-v, Vector3D(-a, -b, c))
    }

}