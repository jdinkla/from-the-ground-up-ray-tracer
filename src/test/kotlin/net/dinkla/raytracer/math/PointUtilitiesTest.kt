package net.dinkla.raytracer.math

import net.dinkla.raytracer.math.PointUtilities.maximum
import net.dinkla.raytracer.math.PointUtilities.minimum
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PointUtilitiesTest {

    val p0 = Point3D.ORIGIN
    val p1 = Point3D.UNIT

    val a = arrayOf(p0, p1)

    @Test
    fun `should return the minimum of an array`() {
        val (x, y, z) = minimum(a, 2)
        assertEquals(0.0, x)
        assertEquals(0.0, y)
        assertEquals(0.0, z)
    }

    @Test
    fun `should return the maximum§ of an array`() {
        val (x, y, z) = maximum(a, 2)
        assertEquals(1.0, x)
        assertEquals(1.0, y)
        assertEquals(1.0, z)
    }

    @Test
    fun minPoints() {
    }

    @Test
    fun maxPoints() {
    }

    @Test
    fun minCoordinates() {
    }

    @Test
    fun maxCoordinates() {
    }
}