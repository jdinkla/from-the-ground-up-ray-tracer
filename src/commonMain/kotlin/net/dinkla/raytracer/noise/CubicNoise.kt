package net.dinkla.raytracer.noise

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.floor

/**
 * Lattice noise with **tricubic** interpolation: the value at a point is a four-knot cubic spline over
 * the surrounding 4×4×4 block of lattice values, evaluated separably along each axis. This gives a
 * smooth, continuous field (continuous first derivative) and the soft organic look the book uses for
 * marble and wood — at higher cost than the trilinear [LinearNoise].
 *
 * The per-axis weights come from the book's `four_knot_spline` Catmull-Rom-style cubic basis.
 *
 * Mirrors Suffern's `CubicNoise` (Ray Tracing from the Ground Up, ch. 31).
 */
class CubicNoise(
    numOctaves: Int = DEFAULT_NUM_OCTAVES,
    lacunarity: Double = DEFAULT_LACUNARITY,
    gain: Double = DEFAULT_GAIN,
    seed: Int = DEFAULT_SEED,
) : LatticeNoise(numOctaves, lacunarity, gain, seed) {
    override fun valueNoise(p: Point3D): Double {
        val ix = floor(p.x).toInt()
        val iy = floor(p.y).toInt()
        val iz = floor(p.z).toInt()

        val fx = p.x - ix
        val fy = p.y - iy
        val fz = p.z - iz

        val xKnots = DoubleArray(SPLINE_KNOTS)
        val yKnots = DoubleArray(SPLINE_KNOTS)
        val zKnots = DoubleArray(SPLINE_KNOTS)

        for (k in 0 until SPLINE_KNOTS) {
            for (j in 0 until SPLINE_KNOTS) {
                for (i in 0 until SPLINE_KNOTS) {
                    xKnots[i] =
                        valueTable[
                            index(
                                ix + i + FIRST_KNOT_OFFSET,
                                iy + j + FIRST_KNOT_OFFSET,
                                iz + k + FIRST_KNOT_OFFSET,
                            ),
                        ]
                }
                yKnots[j] = fourKnotSpline(fx, xKnots)
            }
            zKnots[k] = fourKnotSpline(fy, yKnots)
        }
        return clampNoise(fourKnotSpline(fz, zKnots))
    }

    override fun vectorNoise(p: Point3D): Vector3D {
        val ix = floor(p.x).toInt()
        val iy = floor(p.y).toInt()
        val iz = floor(p.z).toInt()

        val fx = p.x - ix
        val fy = p.y - iy
        val fz = p.z - iz

        val xKnots = Array(SPLINE_KNOTS) { Vector3D.ZERO }
        val yKnots = Array(SPLINE_KNOTS) { Vector3D.ZERO }
        val zKnots = Array(SPLINE_KNOTS) { Vector3D.ZERO }

        for (k in 0 until SPLINE_KNOTS) {
            for (j in 0 until SPLINE_KNOTS) {
                for (i in 0 until SPLINE_KNOTS) {
                    xKnots[i] =
                        gradientTable[
                            index(
                                ix + i + FIRST_KNOT_OFFSET,
                                iy + j + FIRST_KNOT_OFFSET,
                                iz + k + FIRST_KNOT_OFFSET,
                            ),
                        ]
                }
                yKnots[j] = fourKnotSpline(fx, xKnots)
            }
            zKnots[k] = fourKnotSpline(fy, yKnots)
        }
        return fourKnotSpline(fz, zKnots)
    }

    /** Four-knot Catmull-Rom-style cubic spline evaluated at fractional position [t] over [knots]. */
    private fun fourKnotSpline(
        t: Double,
        knots: DoubleArray,
    ): Double {
        val c3 = -HALF * knots[0] + ONE_AND_HALF * knots[1] - ONE_AND_HALF * knots[2] + HALF * knots[3]
        val c2 = knots[0] - TWO_AND_HALF * knots[1] + TWO * knots[2] - HALF * knots[3]
        val c1 = -HALF * knots[0] + HALF * knots[2]
        val c0 = knots[1]
        return ((c3 * t + c2) * t + c1) * t + c0
    }

    private fun fourKnotSpline(
        t: Double,
        knots: Array<Vector3D>,
    ): Vector3D {
        val c3 = knots[0] * -HALF + knots[1] * ONE_AND_HALF - knots[2] * ONE_AND_HALF + knots[3] * HALF
        val c2 = knots[0] - knots[1] * TWO_AND_HALF + knots[2] * TWO - knots[3] * HALF
        val c1 = knots[0] * -HALF + knots[2] * HALF
        val c0 = knots[1]
        return ((c3 * t + c2) * t + c1) * t + c0
    }

    /** Cubic interpolation can slightly overshoot; clamp the scalar field back into `[-1, 1]`. */
    private fun clampNoise(value: Double): Double = value.coerceIn(-1.0, 1.0)

    companion object {
        /** Number of knots (lattice points) the cubic spline samples per axis. */
        private const val SPLINE_KNOTS = 4

        /** Offset of the first of the four knots relative to the cell's lower corner. */
        private const val FIRST_KNOT_OFFSET = -1

        private const val HALF = 0.5
        private const val ONE_AND_HALF = 1.5
        private const val TWO = 2.0
        private const val TWO_AND_HALF = 2.5
    }
}
