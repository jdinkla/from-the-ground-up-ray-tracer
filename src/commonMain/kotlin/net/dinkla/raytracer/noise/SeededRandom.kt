package net.dinkla.raytracer.noise

import net.dinkla.raytracer.math.Vector3D
import kotlin.math.sqrt

/**
 * A tiny deterministic pseudo-random generator used to build the noise value and gradient tables.
 *
 * It is intentionally **not** [kotlin.random.Random]: the noise tables must be byte-identical across
 * runs, platforms and JVM versions so renders are reproducible (TASK-18.3 AC#4). A self-contained
 * linear-congruential generator with fixed multiplier/increment guarantees that — the sequence
 * depends only on the seed. The classic Numerical-Recipes 32-bit LCG constants are used.
 *
 * Not for cryptography or statistical sampling; just a stable, well-spread source for table fill.
 */
class SeededRandom(
    seed: Int,
) {
    private var state: Int = seed

    /** The next raw value of the LCG sequence (full 32-bit range, deterministic from the seed). */
    private fun nextBits(): Int {
        state = state * MULTIPLIER + INCREMENT
        return state
    }

    /** The next pseudo-random double in `[0, 1)`. */
    fun nextUnit(): Double {
        // Use the high bits (better distributed than the low bits of an LCG) mapped to [0, 1).
        val bits = nextBits() ushr SHIFT_TO_POSITIVE
        return bits.toDouble() / POSITIVE_RANGE
    }

    /** The next pseudo-random double in `[-1, 1)`. */
    fun nextSignedUnit(): Double = nextUnit() * 2.0 - 1.0

    /**
     * The next pseudo-random unit vector, approximately uniform on the sphere (rejection-sampled from
     * the cube, then normalised). Degenerate near-zero draws fall back to a fixed axis so the result is
     * always a true unit vector.
     */
    fun nextUnitVector(): Vector3D {
        repeat(MAX_REJECTION_TRIES) {
            val x = nextSignedUnit()
            val y = nextSignedUnit()
            val z = nextSignedUnit()
            val lengthSquared = x * x + y * y + z * z
            if (lengthSquared in MIN_LENGTH_SQUARED..1.0) {
                val length = sqrt(lengthSquared)
                return Vector3D(x / length, y / length, z / length)
            }
        }
        return Vector3D(1.0, 0.0, 0.0)
    }

    companion object {
        // Numerical Recipes 32-bit LCG constants (Park & Miller lineage).
        private const val MULTIPLIER = 1_664_525
        private const val INCREMENT = 1_013_904_223

        /** Drop the sign and low-order bits, keeping the well-distributed high bits. */
        private const val SHIFT_TO_POSITIVE = 8

        /** Number of distinct values produced after [SHIFT_TO_POSITIVE] (`2^24`). */
        private const val POSITIVE_RANGE = (1 shl 24).toDouble()

        /** Cap on rejection-sampling attempts before falling back to a fixed unit vector. */
        private const val MAX_REJECTION_TRIES = 16

        /** Reject near-zero cube draws that would normalise unstably. */
        private const val MIN_LENGTH_SQUARED = 1.0e-6
    }
}
