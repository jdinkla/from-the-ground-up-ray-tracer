package net.dinkla.raytracer.math

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Closed-form real-root solvers for quadratic, cubic and quartic polynomials. They use the classic
 * algebraic methods — reduction to normal form, the depressed cubic via Cardano's formula (with the
 * *casus irreducibilis* trigonometric branch), and the quartic via its resolvent cubic — as used to
 * intersect rays with the [net.dinkla.raytracer.objects.Torus] and similar implicit surfaces.
 *
 * Each solver takes the coefficients in **ascending degree** (`c[0]` is the constant term, `c[n]` the
 * leading term), writes the distinct real roots into [s] (order unspecified), and returns how many it
 * found. The output array must be large enough for the worst case (2 / 3 / 4 roots respectively);
 * extra slots are left untouched.
 */
object Polynomials {
    /** Real cube root, defined for negative arguments (Kotlin's `pow(1/3)` would return NaN there). */
    private fun cbrt(d: Double) =
        if (d < 0.0) {
            -(-d).pow(1.0 / 3.0)
        } else {
            d.pow(1.0 / 3.0)
        }

    /**
     * Solves `c[3]·x³ + c[2]·x² + c[1]·x + c[0] = 0` for its real roots. Reduces to the depressed
     * cubic `y³ + py + q = 0` and applies Cardano's formula, switching to the trigonometric
     * (*casus irreducibilis*) branch when there are three distinct real roots. Returns 1, 2 or 3.
     *
     * @param c coefficients of size 4 (ascending degree); @param s receives the roots (size 3).
     */
    fun solveCubic(
        c: DoubleArray,
        s: DoubleArray,
    ): Int {
        require(c.size == 4 && s.size == 3) {
            "solveCubic expects coefficients of size 4 and solutions of size 3, " +
                "but got c.size=${c.size}, s.size=${s.size}"
        }

        val num: Int
        val A: Double = c[2] / c[3]
        val B: Double = c[1] / c[3]
        val C: Double = c[0] / c[3]

        // normal form: x^3 + Ax^2 + Bx + C = 0
        // substitute x = y - A/3 to eliminate quadric term: x^3 +px + q = 0
        val sq_A = A * A
        val p = 1.0 / 3 * (-1.0 / 3 * sq_A + B)
        val q = 1.0 / 2 * ((2.0 / 27) * A * sq_A - (1.0 / 3) * A * B + C)

        // use Cardano's formula
        val cb_p = p * p * p
        val D = q * q + cb_p

        if (MathUtils.isZero(D)) {
            if (MathUtils.isZero(q)) { // one triple solution
                s[0] = 0.0
                num = 1
            } else { // one single and one double solution
                val u = cbrt(-q)
                s[0] = 2.0 * u
                s[1] = -u
                num = 2
            }
        } else if (D < 0) { // Casus irreducibilis: three real solutions
            val phi = 1.0 / 3.0 * acos(-q / sqrt(-cb_p))
            val t = 2 * sqrt(-p)

            s[0] = t * cos(phi)
            s[1] = -t * cos(phi + MathUtils.PI / 3.0)
            s[2] = -t * cos(phi - MathUtils.PI / 3.0)

            num = 3
        } else { // one real solution
            val sqrt_D = sqrt(D)
            val u = cbrt(sqrt_D - q)
            val v = -cbrt(sqrt_D + q)
            s[0] = u + v
            num = 1
        }

        // resubstitute
        val sub = 1.0 / 3 * A
        var i = 0
        while (i < num) {
            s[i] -= sub
            ++i
        }
        return num
    }

    /**
     * Solves `c[2]·x² + c[1]·x + c[0] = 0` for its real roots via the normal form `x² + px + q = 0`
     * and the discriminant `D = p² - q`. Returns 0 (no real root), 1 (double root) or 2.
     *
     * @param c coefficients of size 3 (ascending degree); @param s receives the roots (size 2).
     */
    fun solveQuadric(
        c: DoubleArray,
        s: DoubleArray,
    ): Int {
        require(c.size == 3 && s.size == 2) {
            "solveQuadric expects coefficients of size 3 and solutions of size 2, " +
                "but got c.size=${c.size}, s.size=${s.size}"
        }

        // normal form: x^2 + px + q = 0
        val p = c[1] / (2 * c[2])
        val q = c[0] / c[2]
        val D = p * p - q

        return when {
            MathUtils.isZero(D) -> {
                s[0] = -p
                1
            }

            D > 0 -> {
                val sqrtD = sqrt(D)
                s[0] = sqrtD - p
                s[1] = -sqrtD - p
                2
            }

            else -> {
                0
            }
        }
    }

    /**
     * Solves `c[4]·x⁴ + c[3]·x³ + c[2]·x² + c[1]·x + c[0] = 0` for its real roots. Reduces to the
     * depressed quartic `y⁴ + py² + qy + r = 0`, then either drops to a cubic when there is no
     * absolute term ([solveQuarticNoAbsoluteTerm]) or factors it through the resolvent cubic into two
     * quadrics ([solveQuarticResolvent]). Returns 0..4.
     *
     * @param c coefficients of size 5 (ascending degree); @param s receives the roots (size 4).
     */
    fun solveQuartic(
        c: DoubleArray,
        s: DoubleArray,
    ): Int {
        require(c.size == 5 && s.size == 4) {
            "solveQuartic expects coefficients of size 5 and solutions of size 4, " +
                "but got c.size=${c.size}, s.size=${s.size}"
        }

        val coeffs4 = DoubleArray(4)
        val coeffs3 = DoubleArray(3)
        val A: Double = c[3] / c[4]
        val B: Double = c[2] / c[4]
        val C: Double = c[1] / c[4]
        val D: Double = c[0] / c[4]
        var num: Int

        // normal form: x^4 + Ax^3 + Bx^2 + Cx + D = 0

        /*  substitute x = y - A/4 to eliminate cubic term:
            x^4 + px^2 + qx + r = 0 */
        val sq_A = A * A
        val p = -3.0 / 8 * sq_A + B
        val q = (1.0 / 8) * sq_A * A - (1.0 / 2) * A * B + C
        val r = (-3.0 / 256) * sq_A * sq_A + (1.0 / 16) * sq_A * B - (1.0 / 4) * A * C + D

        num =
            if (MathUtils.isZero(r)) {
                solveQuarticNoAbsoluteTerm(p, q, s, coeffs4)
            } else {
                solveQuarticResolvent(p, q, r, s, coeffs4, coeffs3)
            }

        // resubstitute
        val sub = 1.0 / 4 * A
        var i = 0
        while (i < num) {
            s[i] -= sub
            ++i
        }
        return num
    }

    /**
     * Solves the depressed quartic when it has no absolute term:
     * y(y^3 + py + q) = 0, i.e. y = 0 plus the roots of the depressed cubic.
     * Writes the roots into [s] and returns their count.
     */
    private fun solveQuarticNoAbsoluteTerm(
        p: Double,
        q: Double,
        s: DoubleArray,
        coeffs4: DoubleArray,
    ): Int {
        coeffs4[0] = q
        coeffs4[1] = p
        coeffs4[2] = 0.0
        coeffs4[3] = 1.0
        val ss = doubleArrayOf(s[0], s[1], s[2])
        var num = solveCubic(coeffs4, ss)
        s[0] = ss[0]
        s[1] = ss[1]
        s[2] = ss[2]
        s[num++] = 0.0
        return num
    }

    /**
     * Solves the general depressed quartic via the resolvent cubic: takes one real root of the
     * resolvent and factors the quartic into two quadrics whose roots are written into [s].
     * Returns the total root count.
     */
    private fun solveQuarticResolvent(
        p: Double,
        q: Double,
        r: Double,
        s: DoubleArray,
        coeffs4: DoubleArray,
        coeffs3: DoubleArray,
    ): Int {
        // solve the resolvent cubic ...
        coeffs4[0] = (1.0 / 2) * r * p - (1.0 / 8) * q * q
        coeffs4[1] = -r
        coeffs4[2] = -1.0 / 2 * p
        coeffs4[3] = 1.0

        val ss = doubleArrayOf(s[0], s[1], s[2])
        solveCubic(coeffs4, ss)
        s[0] = ss[0]
        s[1] = ss[1]
        s[2] = ss[2]

        // ... and take the one real solution ...
        val z = s[0]

        // ... to build two quadric equations
        val u = nonNegativeSqrt(z * z - r)
        val v = nonNegativeSqrt(2 * z - p)

        coeffs3[0] = z - u
        coeffs3[1] = if (q < 0) -v else v
        coeffs3[2] = 1.0

        val ss2 = doubleArrayOf(s[0], s[1])
        var num = solveQuadric(coeffs3, ss2)
        s[0] = ss2[0]
        s[1] = ss2[1]

        coeffs3[0] = z + u
        coeffs3[1] = if (q < 0) v else -v
        coeffs3[2] = 1.0

        // Append the second quadric's roots at offset `num` (the C++ original passed
        // `s + num`); writing back to s[0]/s[1] clobbered the first quadric's roots.
        val ss3 = doubleArrayOf(s[num], s[num + 1])
        val num2 = solveQuadric(coeffs3, ss3)
        s[num] = ss3[0]
        s[num + 1] = ss3[1]
        num += num2
        return num
    }

    /** Returns sqrt(x) for positive x, and 0.0 when x is zero or negative (matching the solver's clamping). */
    private fun nonNegativeSqrt(x: Double): Double =
        when {
            MathUtils.isZero(x) -> 0.0
            x > 0 -> sqrt(x)
            else -> 0.0
        }
}
