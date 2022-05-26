package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.shouldBeApprox


class PolynomialsTest : AnnotationSpec()  {

    private val sol2 = doubleArrayOf(0.0, 0.0)
    private val sol3 = doubleArrayOf(0.0, 0.0, 0.0)
    private val sol4 = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

    @Test
    fun testSolveQuadric() {
        // A*x^2 + B*x + C = 0
        val s1 = doubleArrayOf(-16.0, 0.0, 1.0)
        val num = Polynomials.solveQuadric(s1, sol2)
        num shouldBe 2
        sol2[0] shouldBe 4.0
        sol2[1] shouldBe -4.0
    }

    @Test
    fun testSolveCubic() {
        // A*x^3 + B*x^2 + C*x + D = 0
        var num = Polynomials.solveCubic(doubleArrayOf(0.0, 0.0, 0.0, 1.0), sol3)
        num shouldBe 1
        sol3[0] shouldBe 0.0
    }

    @Test
    fun testSolveCubic2() {
        val s2 = doubleArrayOf(8.0, 0.0, 0.0, 1.0)
        val num = Polynomials.solveCubic(s2, sol3)
        num shouldBe 1
        sol3[0] shouldBe -2.0
    }

    @Test
    fun testSolveCubic3() {
        val s3 = doubleArrayOf(-8.0, 0.0, 0.0, 1.0)
        val num = Polynomials.solveCubic(s3, sol3)
        num shouldBe 1
        sol3[0] shouldBe 2.0
    }

    @Test
    fun testSolveCubic4() {
        val s4 = doubleArrayOf(1.2, -3.2, 1.7, 2.5)
        val num = Polynomials.solveCubic(s4, sol3)
        num shouldBe 1
        sol3[0] shouldBeApprox -1.63938
    }

    @Test
    fun testSolveQuartic() {
        // A*x^4 + B*x^3 + C*x^2+ D*x + E = 0
        val s1 = doubleArrayOf(1.2, -3.2, 1.7, 2.5, -1.02)
        val num = Polynomials.solveQuartic(s1, sol4)
        println("num=$num")
        for (f in sol4) {
            println("f=$f")
        }
    }
}

