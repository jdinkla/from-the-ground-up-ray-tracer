package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals


class MatrixTest {

    @Test
    fun testMatrixMult() {

        val e = Matrix()
        e.m[0][0] = 3.0
        e.m[0][1] = 6.0
        e.m[0][2] = 9.0

        e.m[1][0] = 6.0
        e.m[1][1] = 12.0
        e.m[1][2] = 18.0

        e.m[2][0] = 9.0
        e.m[2][1] = 18.0
        e.m[2][2] = 27.0

        val a = Matrix()
        val b = Matrix()

        for (j in 0..2) {
            for (i in 0..2) {
                a.m[i][j] = (i + 1).toDouble()
                b.m[i][j] = (j + 1).toDouble()
            }
        }

        val c = a.mult(b)
        assertEquals(e, c)
    }
}
