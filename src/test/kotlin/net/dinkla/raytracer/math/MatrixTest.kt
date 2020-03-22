package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

class MatrixTest {

    private val m1 = Matrix(listOf(1.0))
    private val m2 = Matrix(listOf(2.0))
    private val m3 = Matrix(listOf(3.0))

    @Test
    fun `construct from list`() {
        val value = 1.23
        val m = Matrix(listOf(value))
        assertEquals(value, m[0, 0])
        assertEquals(value, m[3, 3])
    }

    @Test
    fun `adding two matrices`() {
        assertEquals(m3, m1 + m2)
        assertNotEquals(m1, m1 + m1)
    }

    @Test
    fun `multiplication of two matrices`() {
        val e = Matrix.identity()
        e[0, 0] = 3.0
        e[0, 1] = 6.0
        e[0, 2] = 9.0

        e[1, 0] = 6.0
        e[1, 1] = 12.0
        e[1, 2] = 18.0

        e[2, 0] = 9.0
        e[2, 1] = 18.0
        e[2, 2] = 27.0

        val a = Matrix.identity()
        val b = Matrix.identity()

        for (j in 0..2) {
            for (i in 0..2) {
                a[i, j] = (i + 1).toDouble()
                b[i, j] = (j + 1).toDouble()
            }
        }
        assertEquals(e, a * b)
    }

    @Test
    fun `multiplication with a Point3D`() {
        val r = 2.0 * 2 + 2 * 3 + 2 * 5 + 2
        val p = Point3D(2.0, 3.0, 5.0)
        assertEquals(Point3D(r, r, r), m2 * p)
    }

    @Test
    fun `multiplication with a Vector3D`() {
        val r = 2.0 * 2 + 2 * 3 + 2 * 5
        val p = Vector3D(2.0, 3.0, 5.0)
        assertEquals(Vector3D(r, r, r), m2 * p)
    }

    @Test
    fun `multiplication with a Normal`() {
        val r = 2.0 * 2 + 2 * 3 + 2 * 5
        val p = Normal(2.0, 3.0, 5.0)
        assertEquals(Normal(r, r, r), m2 * p)
    }

    // TODO all predicate over all indices
    @Test
    fun `division with double value`() {
        val value = 1.5
        val m = m2 / value
        assertEquals(m2[0, 0] / value, m[0, 0])
        assertEquals(m2[3, 3] / value, m[3, 3])
    }

    // TODO all predicate over all indices
    @Test
    fun `create a matrix filled with zeros`() {
        val m = Matrix.zero()
        assertEquals(0.0, m[0, 0])
        assertEquals(0.0, m[1, 0])
        assertEquals(0.0, m[3, 2])
    }

}
