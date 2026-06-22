package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Polynomials
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.equals
import java.util.Objects

/**
 * A [Torus] (sweep radius [a], tube radius [b], centred at the origin in the xz-plane) restricted to
 * an azimuth wedge `[phiMin, phiMax]` (radians) around the y-axis. A full torus corresponds to
 * `phi 0..2π`. Reuses the quartic intersection of the full torus and rejects roots whose hit point
 * falls outside the wedge.
 *
 * Analogous to [Torus]; see Suffern, *Ray Tracing from the Ground Up*, ch. 19.
 */
class PartTorus(
    val a: Double,
    val b: Double,
    val phiMin: Double = 0.0,
    val phiMax: Double = PartAngles.TWO_PI,
) : GeometricObject() {
    init {
        boundingBox = BBox(Point3D(-a - b, -b, -a - b), Point3D(a + b, b, a + b))
    }

    private fun isInWedge(p: Point3D): Boolean =
        PartAngles.inPhiRange(PartAngles.phi(p.x, p.z), phiMin, phiMax)

    private fun quarticCoeffs(ray: Ray): DoubleArray {
        val x1 = ray.origin.x
        val y1 = ray.origin.y
        val z1 = ray.origin.z
        val d1 = ray.direction.x
        val d2 = ray.direction.y
        val d3 = ray.direction.z

        val sumDSqrd = d1 * d1 + d2 * d2 + d3 * d3
        val e = x1 * x1 + y1 * y1 + z1 * z1 - a * a - b * b
        val f = x1 * d1 + y1 * d2 + z1 * d3
        val fourASqrd = FOUR * a * a

        return doubleArrayOf(
            e * e - fourASqrd * (b * b - y1 * y1),
            FOUR * f * e + TWO * fourASqrd * y1 * d2,
            TWO * sumDSqrd * e + FOUR * f * f + fourASqrd * d2 * d2,
            FOUR * sumDSqrd * f,
            sumDSqrd * sumDSqrd,
        )
    }

    /** True when [t] is a forward intersection whose hit point lies in the kept azimuth wedge. */
    private fun isValid(
        t: Double,
        ray: Ray,
    ): Boolean = t > MathUtils.K_EPSILON && isInWedge(ray.linear(t))

    /** Nearest positive root whose hit point lies in the wedge, or `null` if none. */
    private fun nearestValidRoot(ray: Ray): Double? {
        if (!boundingBox.isHit(ray)) {
            return null
        }
        val roots = DoubleArray(MAX_ROOTS)
        val numRealRoots = Polynomials.solveQuartic(quarticCoeffs(ray), roots)
        var best: Double? = null
        for (j in 0 until numRealRoots) {
            val t = roots[j]
            if (isValid(t, ray) && (best == null || t < best)) {
                best = t
            }
        }
        return best
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val t = nearestValidRoot(ray) ?: return false
        sr.t = t
        sr.normal = computeNormal(ray.linear(t))
        return true
    }

    override fun shadowHit(ray: Ray): Shadow =
        when (val t = nearestValidRoot(ray)) {
            null -> Shadow.None
            else -> Shadow.Hit(t)
        }

    private fun computeNormal(p: Point3D): Normal {
        val paramSquared = a * a + b * b
        val sumSquared = p.x * p.x + p.y * p.y + p.z * p.z
        val diff = sumSquared - paramSquared
        val x = FOUR * p.x * diff
        val y = FOUR * p.y * (diff + TWO * a * a)
        val z = FOUR * p.z * diff
        return Normal(x, y, z).normalize()
    }

    override fun equals(other: Any?): Boolean =
        this.equals<PartTorus>(other) { p, q ->
            p.a == q.a && p.b == q.b && p.phiMin == q.phiMin && p.phiMax == q.phiMax
        }

    override fun hashCode(): Int = Objects.hash(a, b, phiMin, phiMax)

    override fun toString(): String = "PartTorus($a, $b, phi=[$phiMin,$phiMax])"

    private companion object {
        const val TWO = 2.0
        const val FOUR = 4.0
        const val MAX_ROOTS = 4
    }
}
