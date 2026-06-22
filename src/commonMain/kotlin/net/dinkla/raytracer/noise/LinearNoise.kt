package net.dinkla.raytracer.noise

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.floor

/**
 * Lattice noise with **trilinear** interpolation: the value at a point is the linear blend of the
 * eight surrounding lattice values (or gradient vectors), weighted by the point's fractional position
 * within its lattice cell. Cheaper than [CubicNoise] but with visible first-derivative discontinuities
 * at the cell boundaries (the classic "boxy" linear-noise look).
 *
 * Mirrors Suffern's `LinearNoise` (Ray Tracing from the Ground Up, ch. 31).
 */
class LinearNoise(
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

        // Corner values c[dx][dy][dz] of the unit lattice cell containing p.
        fun corner(
            dx: Int,
            dy: Int,
            dz: Int,
        ): Double = valueTable[index(ix + dx, iy + dy, iz + dz)]

        val x00 = lerp(fx, corner(0, 0, 0), corner(1, 0, 0))
        val x10 = lerp(fx, corner(0, 1, 0), corner(1, 1, 0))
        val x01 = lerp(fx, corner(0, 0, 1), corner(1, 0, 1))
        val x11 = lerp(fx, corner(0, 1, 1), corner(1, 1, 1))

        val y0 = lerp(fy, x00, x10)
        val y1 = lerp(fy, x01, x11)

        return lerp(fz, y0, y1)
    }

    override fun vectorNoise(p: Point3D): Vector3D {
        val ix = floor(p.x).toInt()
        val iy = floor(p.y).toInt()
        val iz = floor(p.z).toInt()

        val fx = p.x - ix
        val fy = p.y - iy
        val fz = p.z - iz

        fun corner(
            dx: Int,
            dy: Int,
            dz: Int,
        ): Vector3D = gradientTable[index(ix + dx, iy + dy, iz + dz)]

        val x00 = lerp(fx, corner(0, 0, 0), corner(1, 0, 0))
        val x10 = lerp(fx, corner(0, 1, 0), corner(1, 1, 0))
        val x01 = lerp(fx, corner(0, 0, 1), corner(1, 0, 1))
        val x11 = lerp(fx, corner(0, 1, 1), corner(1, 1, 1))

        val y0 = lerp(fy, x00, x10)
        val y1 = lerp(fy, x01, x11)

        return lerp(fz, y0, y1)
    }

    private fun lerp(
        t: Double,
        a: Double,
        b: Double,
    ): Double = a + t * (b - a)

    private fun lerp(
        t: Double,
        a: Vector3D,
        b: Vector3D,
    ): Vector3D = a + (b - a) * t
}
