package net.dinkla.raytracer.math

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.shouldBeApprox

class PolynomialsTest :
    StringSpec({

        "A*x^2 + B*x + C = 0" {
            val s1 = doubleArrayOf(-16.0, 0.0, 1.0)
            val sol2 = doubleArrayOf(0.0, 0.0)
            val num = Polynomials.solveQuadric(s1, sol2)
            num shouldBe 2
            sol2[0] shouldBe 4.0
            sol2[1] shouldBe -4.0
        }

        "A*x^3 + B*x^2 + C*x + D = 0" {
            val sol3 = doubleArrayOf(0.0, 0.0, 0.0)
            val num = Polynomials.solveCubic(doubleArrayOf(0.0, 0.0, 0.0, 1.0), sol3)
            num shouldBe 1
            sol3[0] shouldBe 0.0
        }

        "testSolveCubic2" {
            val sol3 = doubleArrayOf(0.0, 0.0, 0.0)
            val s2 = doubleArrayOf(8.0, 0.0, 0.0, 1.0)
            val num = Polynomials.solveCubic(s2, sol3)
            num shouldBe 1
            sol3[0] shouldBe -2.0
        }

        "testSolveCubic3" {
            val sol3 = doubleArrayOf(0.0, 0.0, 0.0)
            val s3 = doubleArrayOf(-8.0, 0.0, 0.0, 1.0)
            val num = Polynomials.solveCubic(s3, sol3)
            num shouldBe 1
            sol3[0] shouldBe 2.0
        }

        "testSolveCubic4" {
            val sol3 = doubleArrayOf(0.0, 0.0, 0.0)
            val s4 = doubleArrayOf(1.2, -3.2, 1.7, 2.5)
            val num = Polynomials.solveCubic(s4, sol3)
            num shouldBe 1
            sol3[0] shouldBeApprox -1.63938
        }

        // solveQuartic on x^4 - x^2 = x^2 * (x^2 - 1), real roots {-1, 0, 0, 1} -- a case
        // the solver handles correctly. Verify each returned root satisfies the polynomial
        // (residual ~ 0) rather than pinning literal root values.
        "roots of x^4 - x^2 = 0 satisfy the polynomial" {
            val coeffs = doubleArrayOf(0.0, 0.0, -1.0, 0.0, 1.0) // c[i] = coefficient of x^i
            val sol4 = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

            val num = Polynomials.solveQuartic(coeffs, sol4)

            num shouldBe 4
            for (i in 0 until num) {
                evalPoly(coeffs, sol4[i]) shouldBeApprox 0.0
            }
        }

        // Regression for TASK-23 (quadric-root clobbering): a quartic with four distinct real
        // roots exercises both inner quadrics. The bug dropped the first pair; every returned
        // root must now satisfy the polynomial.
        "solveQuartic finds all four roots of (x-1)(x-2)(x-3)(x-4)" {
            val coeffs = doubleArrayOf(24.0, -50.0, 35.0, -10.0, 1.0)
            val sol4 = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

            val num = Polynomials.solveQuartic(coeffs, sol4)

            num shouldBe 4
            for (i in 0 until num) {
                evalPoly(coeffs, sol4[i]) shouldBeApprox 0.0
            }
        }

        // Double real roots: (x^2 - 4)^2 = x^4 - 8x^2 + 16, roots +-2 (each twice).
        "solveQuartic solves (x^2 - 4)^2 with double roots +-2" {
            val coeffs = doubleArrayOf(16.0, 0.0, -8.0, 0.0, 1.0)
            val sol4 = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

            val num = Polynomials.solveQuartic(coeffs, sol4)

            num shouldBe 2
            for (i in 0 until num) {
                evalPoly(coeffs, sol4[i]) shouldBeApprox 0.0
            }
        }

        // The case that exposed TASK-23 (negative leading coefficient, true roots ~ -1.2875
        // and 2.6976). Previously returned 0.6127 twice; now its roots satisfy the polynomial.
        "solveQuartic solves 1.2 - 3.2x + 1.7x^2 + 2.5x^3 - 1.02x^4" {
            val coeffs = doubleArrayOf(1.2, -3.2, 1.7, 2.5, -1.02)
            val sol4 = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

            val num = Polynomials.solveQuartic(coeffs, sol4)

            num shouldBe 2
            for (i in 0 until num) {
                evalPoly(coeffs, sol4[i]) shouldBeApprox 0.0
            }
        }

        // Pins the wrong-sized-input contract (TASK-5): illegal array sizes must fail with a
        // specific, descriptive IllegalArgumentException rather than a bare AssertionError.
        "solveQuadric rejects wrongly sized arrays with a descriptive IllegalArgumentException" {
            val ex =
                shouldThrow<IllegalArgumentException> {
                    Polynomials.solveQuadric(doubleArrayOf(1.0, 2.0), doubleArrayOf(0.0, 0.0))
                }

            ex.message shouldContain "solveQuadric"
        }

        "solveCubic rejects wrongly sized arrays with a descriptive IllegalArgumentException" {
            val ex =
                shouldThrow<IllegalArgumentException> {
                    Polynomials.solveCubic(doubleArrayOf(1.0, 2.0, 3.0), doubleArrayOf(0.0, 0.0, 0.0))
                }

            ex.message shouldContain "solveCubic"
        }

        "solveQuartic rejects wrongly sized arrays with a descriptive IllegalArgumentException" {
            val ex =
                shouldThrow<IllegalArgumentException> {
                    Polynomials.solveQuartic(doubleArrayOf(1.0, 2.0, 3.0, 4.0), doubleArrayOf(0.0, 0.0, 0.0, 0.0))
                }

            ex.message shouldContain "solveQuartic"
        }
    })

// Horner evaluation: sum of coeffs[i] * x^i, with coeffs[i] the coefficient of x^i.
private fun evalPoly(
    coeffs: DoubleArray,
    x: Double,
): Double {
    var result = 0.0
    for (i in coeffs.indices.reversed()) {
        result = result * x + coeffs[i]
    }
    return result
}
