package net.dinkla.raytracer.noise

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.times
import kotlin.math.abs

/**
 * Lattice noise: pseudo-random noise defined over the integer lattice and interpolated between
 * lattice points, the foundation of the book's procedural noise textures (marble, wood, turbulence).
 *
 * A fixed [PERMUTATION] table hashes an integer lattice cell `(ix, iy, iz)` into an index in
 * `[0, TABLE_SIZE)`; that index selects a pseudo-random scalar from [valueTable] (value noise) or a
 * pseudo-random unit vector from [gradientTable] (gradient noise). Subclasses interpolate the values
 * of the surrounding lattice points to produce a smooth field: [LinearNoise] uses trilinear
 * interpolation, [CubicNoise] uses tricubic interpolation.
 *
 * **Determinism.** Both tables are built once, deterministically, from a fixed [seed] using a small
 * seeded linear-congruential generator — never `kotlin.random.Random()` or wall-clock state. The same
 * point therefore always yields the same noise value, so renders are reproducible. The permutation
 * table is the classic 256-entry Perlin table.
 *
 * On top of single-frequency noise, this class provides the standard fractal combinators:
 * [fractalSum]/[fbm] (a sum of octaves of increasing frequency and decreasing amplitude) and
 * [turbulence] (the same sum over the *absolute value* of the noise, giving the billowy look used by
 * marble and wood).
 *
 * Mirrors Suffern's `LatticeNoise` (Ray Tracing from the Ground Up, ch. 31).
 */
abstract class LatticeNoise(
    val numOctaves: Int = DEFAULT_NUM_OCTAVES,
    val lacunarity: Double = DEFAULT_LACUNARITY,
    val gain: Double = DEFAULT_GAIN,
    seed: Int = DEFAULT_SEED,
) {
    /** Per-lattice-point scalar values in `[-1, 1]`, indexed via the permutation hash. */
    protected val valueTable: DoubleArray

    /** Per-lattice-point unit gradient vectors, indexed via the permutation hash. */
    protected val gradientTable: Array<Vector3D>

    init {
        val rng = SeededRandom(seed)
        valueTable = DoubleArray(TABLE_SIZE) { rng.nextSignedUnit() }
        gradientTable = Array(TABLE_SIZE) { rng.nextUnitVector() }
    }

    /** The smooth (interpolated) value-noise field sampled at [p]; lies in `[-1, 1]`. */
    abstract fun valueNoise(p: Point3D): Double

    /** The smooth (interpolated) gradient-noise field sampled at [p]; a vector field. */
    abstract fun vectorNoise(p: Point3D): Vector3D

    /**
     * Fractal sum (fractional Brownian motion, *fBm*): the sum of [numOctaves] octaves of
     * [valueNoise], each octave at [lacunarity]× the previous frequency and [gain]× the amplitude.
     * The raw sum is rescaled to `[-1, 1]` by dividing by the maximum possible amplitude sum, so the
     * output range is independent of the octave count.
     */
    fun fractalSum(p: Point3D): Double {
        var amplitude = 1.0
        var frequency = 1.0
        var sum = 0.0
        var maxAmplitude = 0.0
        repeat(numOctaves) {
            sum += amplitude * valueNoise(frequency * p)
            maxAmplitude += amplitude
            amplitude *= gain
            frequency *= lacunarity
        }
        return sum / maxAmplitude
    }

    /** Alias for [fractalSum]: the textbook name for the same fractional-Brownian-motion sum. */
    fun fbm(p: Point3D): Double = fractalSum(p)

    /**
     * Turbulence: the sum of [numOctaves] octaves of `|valueNoise|`. Because each term is an absolute
     * value the result is always `>= 0`; it is rescaled to `[0, 1]` by the maximum amplitude sum. The
     * creases where the noise crosses zero produce the billowy veins used by marble and wood.
     */
    fun turbulence(p: Point3D): Double {
        var amplitude = 1.0
        var frequency = 1.0
        var sum = 0.0
        var maxAmplitude = 0.0
        repeat(numOctaves) {
            sum += amplitude * abs(valueNoise(frequency * p))
            maxAmplitude += amplitude
            amplitude *= gain
            frequency *= lacunarity
        }
        return sum / maxAmplitude
    }

    /**
     * Hashes the integer lattice cell ([ix], [iy], [iz]) to a table index in `[0, TABLE_SIZE)` via the
     * fixed [PERMUTATION] table, folding each coordinate into `[0, TABLE_SIZE)` first. Three nested
     * lookups give a well-mixed hash that is stable across calls (the source of determinism at the
     * lattice level).
     */
    protected fun index(
        ix: Int,
        iy: Int,
        iz: Int,
    ): Int {
        val px = PERMUTATION[ix and TABLE_MASK]
        val py = PERMUTATION[(px + iy) and TABLE_MASK]
        val pz = PERMUTATION[(py + iz) and TABLE_MASK]
        return pz
    }

    companion object {
        /** Number of lattice values/gradients; equals the permutation table length. */
        const val TABLE_SIZE = 256

        /** Mask for folding any integer into `[0, TABLE_SIZE)` (works because [TABLE_SIZE] is a power of two). */
        const val TABLE_MASK = TABLE_SIZE - 1

        /** Default number of octaves summed by [fractalSum]/[turbulence]. */
        const val DEFAULT_NUM_OCTAVES = 4

        /** Default frequency multiplier between successive octaves. */
        const val DEFAULT_LACUNARITY = 2.0

        /** Default amplitude multiplier between successive octaves. */
        const val DEFAULT_GAIN = 0.5

        /** Fixed seed for the value/gradient tables; chosen once so all renders are reproducible. */
        const val DEFAULT_SEED = 253

        /**
         * The classic 256-entry Perlin permutation table: a fixed shuffle of `0..255`. Using the
         * book's well-known constant table (rather than a runtime shuffle) keeps the lattice hash
         * identical across runs and platforms.
         */
        val PERMUTATION =
            intArrayOf(
                225, 155, 210, 108, 175, 199, 221, 144, 203, 116, 70, 213, 69, 158, 33, 252,
                5, 82, 173, 133, 222, 139, 174, 27, 9, 71, 90, 246, 75, 130, 91, 191,
                169, 138, 2, 151, 194, 235, 81, 7, 25, 113, 228, 159, 205, 253, 134, 142,
                248, 65, 224, 217, 22, 121, 229, 63, 89, 103, 96, 104, 156, 17, 201, 129,
                36, 8, 165, 110, 237, 117, 231, 56, 132, 211, 152, 20, 181, 111, 239, 218,
                170, 163, 51, 172, 157, 47, 80, 212, 176, 250, 87, 49, 99, 242, 136, 189,
                162, 115, 44, 43, 124, 94, 150, 16, 141, 247, 32, 10, 198, 223, 255, 72,
                53, 131, 84, 57, 220, 197, 58, 50, 208, 11, 241, 28, 3, 192, 62, 202,
                18, 215, 153, 24, 76, 41, 15, 179, 39, 46, 55, 6, 128, 167, 23, 188,
                106, 34, 187, 140, 164, 73, 112, 182, 244, 195, 227, 13, 35, 77, 196, 185,
                26, 200, 226, 119, 31, 123, 168, 125, 249, 68, 183, 230, 177, 135, 160, 180,
                12, 1, 243, 148, 102, 166, 38, 238, 251, 37, 240, 126, 64, 74, 161, 40,
                184, 149, 171, 178, 101, 66, 29, 59, 146, 61, 254, 107, 42, 86, 154, 4,
                236, 232, 120, 21, 233, 209, 45, 98, 193, 114, 78, 19, 206, 14, 118, 127,
                48, 79, 147, 85, 30, 207, 219, 54, 88, 234, 190, 122, 95, 67, 143, 109,
                137, 214, 145, 93, 92, 100, 245, 0, 216, 186, 60, 83, 105, 97, 204, 52,
            )
    }
}
