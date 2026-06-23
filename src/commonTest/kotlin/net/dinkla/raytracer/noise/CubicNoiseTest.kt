package net.dinkla.raytracer.noise

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.math.Point3D

/**
 * Tricubic lattice noise. [LatticeNoiseTest] checks the shared fractal combinators against the linear
 * variant; this class pins the cubic-specific seams the linear tests do not reach: the tricubic
 * `valueNoise` spline path (including the `clampNoise` overshoot guard the cubic spline actually
 * needs, unlike trilinear interpolation), and the cubic `vectorNoise` spline path. Expectations are
 * derived from the noise contract, not round-tripped: determinism for a fixed point/seed, the field
 * genuinely varying across cells, and the scalar field staying inside `[-1, 1]` after clamping.
 */
class CubicNoiseTest :
    StringSpec({

        // Arbitrary non-lattice points spread across several cells, plus an exact lattice point.
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

        "cubic value noise is deterministic for the same point" {
            val noise = CubicNoise()
            val p = Point3D(1.234, -5.678, 9.0)

            noise.valueNoise(p) shouldBe noise.valueNoise(p)
        }

        "two cubic noises with the same seed produce identical values" {
            val a = CubicNoise(seed = 17)
            val b = CubicNoise(seed = 17)
            val p = Point3D(2.5, -3.5, 7.25)

            a.valueNoise(p) shouldBe b.valueNoise(p)
        }

        "cubic value noise varies across distinct cells" {
            // If the spline collapsed to a constant the field would be useless; two well-separated
            // points must differ, proving the tricubic interpolation actually reads the lattice.
            val noise = CubicNoise()

            noise.valueNoise(Point3D(0.3, 0.3, 0.3)) shouldNotBe noise.valueNoise(Point3D(7.4, -2.1, 5.6))
        }

        "cubic value noise stays within [-1, 1] after the overshoot clamp" {
            // The cubic spline can ring past the corner values; clampNoise coerces it back. Checking a
            // spread of points exercises both the clamped and the unclamped branch.
            val noise = CubicNoise()

            samplePoints.forEach { p ->
                val v = noise.valueNoise(p)
                v shouldBeGreaterThanOrEqual -1.0
                v shouldBeLessThanOrEqual 1.0
            }
        }

        "cubic gradient (vector) noise is deterministic for the same point" {
            val noise = CubicNoise()
            val p = Point3D(0.6, -0.4, 2.1)

            noise.vectorNoise(p) shouldBe noise.vectorNoise(p)
        }

        "cubic gradient noise varies across distinct cells" {
            val noise = CubicNoise()

            noise.vectorNoise(Point3D(0.2, 0.2, 0.2)) shouldNotBe noise.vectorNoise(Point3D(9.1, -4.3, 6.7))
        }

        "cubic fBm stays within [-1, 1] across many points and octave counts" {
            listOf(1, 2, 4, 8).forEach { octaves ->
                val noise = CubicNoise(numOctaves = octaves)
                samplePoints.forEach { p ->
                    val v = noise.fbm(p)
                    v shouldBeGreaterThanOrEqual -1.0
                    v shouldBeLessThanOrEqual 1.0
                }
            }
        }

        "cubic turbulence is non-negative and within [0, 1]" {
            val noise = CubicNoise(numOctaves = 6)

            samplePoints.forEach { p ->
                val v = noise.turbulence(p)
                v shouldBeGreaterThanOrEqual 0.0
                v shouldBeLessThanOrEqual 1.0
            }
        }
    })
