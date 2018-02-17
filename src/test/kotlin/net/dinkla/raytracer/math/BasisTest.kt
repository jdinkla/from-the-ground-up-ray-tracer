package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

// TODO how to test this without re-implementing the math
internal class BasisTest {

    private val eye = Point3D(1, 2, 3)
    private val lookAt = Point3D(3, 2, 1)
    private val up = Vector3D(0, 1, 0)
    private val b = Basis(eye, lookAt, up)

    @Test
    fun `construct an instance`() {
        assertNotNull(b.u)
        assertNotNull(b.v)
        assertNotNull(b.w)
    }

    @Test
    fun pm() {
    }

    @Test
    fun pp() {
    }
}