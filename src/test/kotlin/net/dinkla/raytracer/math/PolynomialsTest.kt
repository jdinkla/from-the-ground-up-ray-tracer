package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals


class PolynomialsTest {


    @Test
    @Throws(Exception::class)
    fun testSolveQuadric() {
        // A*x^2 + B*x + C = 0
        val s1 = doubleArrayOf(-16.0, 0.0, 1.0)
        val sol = doubleArrayOf(0.0, 0.0)

        val num = Polynomials.solveQuadric(s1, sol)

        assertEquals(2, num)
        assertEquals(4.0, sol[0])
        assertEquals(-4.0, sol[1])
    }

    @Test
    @Throws(Exception::class)
    fun testSolveQuartic() {

        // A*x^4 + B*x^3 + C*x^2+ D*x + E = 0
        val s1 = doubleArrayOf(1.2, -3.2, 1.7, 2.5, -1.02)
        val sol = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

        val num = Polynomials.solveQuartic(s1, sol)

        println("num=$num")
        for (f in sol) {
            println("f=$f")
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSolveCubic() {

        // A*x^3 + B*x^2 + C*x + D = 0
        val s1 = doubleArrayOf(0.0, 0.0, 0.0, 1.0)
        val sol = doubleArrayOf(0.0, 0.0, 0.0)

        var num = Polynomials.solveCubic(s1, sol)
        assertEquals(1, num)
        assertEquals(0.0, sol[0])

        val s2 = doubleArrayOf(8.0, 0.0, 0.0, 1.0)
        num = Polynomials.solveCubic(s2, sol)
        assertEquals(1, num)
        assertEquals(-2.0, sol[0])

        val s3 = doubleArrayOf(-8.0, 0.0, 0.0, 1.0)
        num = Polynomials.solveCubic(s3, sol)
        assertEquals(1, num)
        assertEquals(2.0, sol[0])

        val s4 = doubleArrayOf(1.2, -3.2, 1.7, 2.5)
        num = Polynomials.solveCubic(s4, sol)
        assertEquals(1, num)
        assertEquals(-1.63938, sol[0], 0.00001)

        /*
        System.out.println("num=" + num);
        for (double f : sol) {
            System.out.println("f=" + f);
        }
        */

    }
}
