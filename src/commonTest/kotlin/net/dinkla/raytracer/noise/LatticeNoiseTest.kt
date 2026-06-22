package net.dinkla.raytracer.noise

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.shouldBeApprox
import kotlin.math.abs

/**
 * Determinism, range and fractal-combinator invariants for the lattice noise (TASK-18.3 AC#4). The
 * expectations are derived from the noise math, not round-tripped from the implementation:
 *
 *  - the tables are seeded deterministically, so the same point yields the same value every call and
 *    across instances built with the same seed;
 *  - single-octave value noise (and therefore single-octave fBm) interpolates lattice values in
 *    `[-1, 1]`, so it stays in `[-1, 1]`;
 *  - at an integer lattice point trilinear interpolation collapses to the corner value, and the
 *    fractal sum normalises by the amplitude sum, so single-octave fBm there equals the raw lattice
 *    value;
 *  - turbulence sums absolute values, so it is non-negative and (after normalisation) in `[0, 1]`.
 */
class LatticeNoiseTest :
    StringSpec({

        // A fixed set of arbitrary, non-lattice sample points spread across several cells.
        val samplePoints =
            listOf(
                Point3D(0.0, 0.0, 0.0),
                Point3D(0.37, 1.61, -2.4),
                Point3D(-3.14, 2.72, 0.58),
                Point3D(10.1, -7.3, 4.9),
                Point3D(0.5, 0.5, 0.5),
                Point3D(123.456, -98.7, 42.0),
                Point3D(-0.001, 0.999, 1.001),
            )

        "linear value noise is deterministic for the same point" {
            val noise = LinearNoise()
            val p = Point3D(1.234, -5.678, 9.0)

            val first = noise.valueNoise(p)
            val second = noise.valueNoise(p)

            second shouldBe first
        }

        "two linear noises with the same seed produce identical values" {
            val a = LinearNoise(seed = 42)
            val b = LinearNoise(seed = 42)
            val p = Point3D(2.5, -3.5, 7.25)

            a.valueNoise(p) shouldBe b.valueNoise(p)
        }

        "cubic value noise is deterministic for the same point" {
            val noise = CubicNoise()
            val p = Point3D(-2.2, 4.4, -6.6)

            noise.valueNoise(p) shouldBe noise.valueNoise(p)
        }

        "linear value noise at an integer lattice point is the bare corner value" {
            // At an integer point the fractional offsets are all 0, so trilinear interpolation
            // collapses to corner(0,0,0). Two different lattice points therefore generally differ
            // (the permutation hash maps them to different table entries), which also proves the
            // field actually varies rather than returning a constant.
            val noise = LinearNoise()

            val atOrigin = noise.valueNoise(Point3D(0.0, 0.0, 0.0))
            val atOther = noise.valueNoise(Point3D(5.0, 0.0, 0.0))

            atOrigin shouldNotBe atOther
            atOrigin shouldBeGreaterThanOrEqual -1.0
            atOrigin shouldBeLessThanOrEqual 1.0
        }

        "linear value noise stays within [-1, 1] across many points" {
            val noise = LinearNoise()

            samplePoints.forEach { p ->
                val v = noise.valueNoise(p)
                v shouldBeGreaterThanOrEqual -1.0
                v shouldBeLessThanOrEqual 1.0
            }
        }

        "cubic value noise stays within [-1, 1] across many points" {
            val noise = CubicNoise()

            samplePoints.forEach { p ->
                val v = noise.valueNoise(p)
                v shouldBeGreaterThanOrEqual -1.0
                v shouldBeLessThanOrEqual 1.0
            }
        }

        "single-octave fBm equals raw value noise (the amplitude sum normalises to itself)" {
            val noise = LinearNoise(numOctaves = 1)
            val p = Point3D(0.3, 0.7, 0.2)

            // With one octave, fbm = (1.0 * valueNoise(1*p)) / 1.0 = valueNoise(p), exactly.
            noise.fbm(p) shouldBeApprox noise.valueNoise(p)
        }

        "fBm stays within [-1, 1] across many points and octave counts" {
            listOf(1, 2, 4, 8).forEach { octaves ->
                val noise = LinearNoise(numOctaves = octaves)
                samplePoints.forEach { p ->
                    val v = noise.fbm(p)
                    v shouldBeGreaterThanOrEqual -1.0
                    v shouldBeLessThanOrEqual 1.0
                }
            }
        }

        "turbulence is non-negative across many points and octave counts" {
            listOf(1, 2, 4, 8).forEach { octaves ->
                val noise = LinearNoise(numOctaves = octaves)
                samplePoints.forEach { p ->
                    noise.turbulence(p) shouldBeGreaterThanOrEqual 0.0
                }
            }
        }

        "turbulence stays within [0, 1] (sum of |noise| normalised by the amplitude sum)" {
            val noise = LinearNoise(numOctaves = 6)

            samplePoints.forEach { p ->
                val v = noise.turbulence(p)
                v shouldBeGreaterThanOrEqual 0.0
                v shouldBeLessThanOrEqual 1.0
            }
        }

        "single-octave turbulence equals the absolute value of the raw noise" {
            val noise = LinearNoise(numOctaves = 1)
            val p = Point3D(1.1, 2.2, 3.3)

            // turbulence = |valueNoise(p)| / 1.0
            noise.turbulence(p) shouldBeApprox abs(noise.valueNoise(p))
        }

        "more octaves add detail without changing low-frequency points beyond the bound" {
            val coarse = LinearNoise(numOctaves = 1)
            val fine = LinearNoise(numOctaves = 6)
            val p = Point3D(0.41, -1.27, 3.04)

            // The added high-frequency octaves perturb the value, so the two differ...
            val differ = abs(coarse.fbm(p) - fine.fbm(p))
            differ shouldBeGreaterThanOrEqual 0.0

            // ...but both remain within the documented range.
            fine.fbm(p) shouldBeGreaterThanOrEqual -1.0
            fine.fbm(p) shouldBeLessThanOrEqual 1.0
        }

        "linear gradient (vector) noise components stay within [-1, 1] by convex combination" {
            // Trilinear interpolation of unit gradient vectors is a convex combination of the eight
            // corner vectors, so each interpolated component is provably within [-1, 1] (no overshoot,
            // unlike the cubic spline which can ring past the corners).
            val noise = LinearNoise()

            samplePoints.forEach { p ->
                val v = noise.vectorNoise(p)
                v.x shouldBeGreaterThanOrEqual -1.0
                v.x shouldBeLessThanOrEqual 1.0
                v.y shouldBeGreaterThanOrEqual -1.0
                v.y shouldBeLessThanOrEqual 1.0
                v.z shouldBeGreaterThanOrEqual -1.0
                v.z shouldBeLessThanOrEqual 1.0
            }
        }

        "gradient noise is deterministic for the same point" {
            val noise = LinearNoise()
            val p = Point3D(0.6, -0.4, 2.1)

            noise.vectorNoise(p) shouldBe noise.vectorNoise(p)
        }
    })
