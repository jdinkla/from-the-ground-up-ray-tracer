package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RayTest {

    private val origin = Point3D(1.0, 1.0, 1.0)
    private val direction = Vector3D.UP
    private val ray = Ray(origin, direction)

    @Test
    fun `construct from ray`() {
        val ray2 = Ray(ray)
        assertEquals(origin, ray2.origin)
        assertEquals(direction, ray2.direction)
    }

    @Test
    fun linear() {
        assertEquals(origin + (direction * 0.5), ray.linear(0.5))
    }
}