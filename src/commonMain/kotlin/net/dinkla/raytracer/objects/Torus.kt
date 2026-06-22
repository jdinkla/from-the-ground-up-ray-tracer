package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Polynomials
import net.dinkla.raytracer.math.Ray
import kotlin.math.abs

data class Torus(
    val a: Double,
    val b: Double,
) : GeometricObject() {
    init {
        boundingBox = BBox(Point3D(-a - b, -b, -a - b), Point3D(a + b, b, a + b))
    }

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

    /**
     * The torus implicit-equation residual at point [p]; zero exactly on the surface. Used to reject
     * the spurious roots the quartic solver can return for ill-conditioned (near-axis) rays.
     */
    private fun surfaceResidual(p: Point3D): Double {
        val s = p.x * p.x + p.y * p.y + p.z * p.z + a * a - b * b
        return s * s - FOUR * a * a * (p.x * p.x + p.z * p.z)
    }

    /**
     * The nearest forward intersection distance, or `null` if the ray misses. Each candidate root from
     * [Polynomials.solveQuartic] is Newton-polished against the quartic and then validated against the
     * torus surface: roots whose hit point does not lie on the surface (the solver's phantom roots) are
     * rejected. See TASK-29.
     */
    private fun nearestValidRoot(ray: Ray): Double? {
        if (!boundingBox.isHit(ray)) {
            return null
        }
        val coeffs = quarticCoeffs(ray)
        val roots = DoubleArray(MAX_ROOTS)
        val numRealRoots = Polynomials.solveQuartic(coeffs, roots)
        var best: Double? = null
        for (j in 0 until numRealRoots) {
            val t = Polynomials.polishRoot(coeffs, roots[j])
            if (isValid(t, ray) && (best == null || t < best)) {
                best = t
            }
        }
        return best
    }

    /** True when [t] is a forward intersection whose hit point actually lies on the torus surface. */
    private fun isValid(
        t: Double,
        ray: Ray,
    ): Boolean = t > MathUtils.K_EPSILON && abs(surfaceResidual(ray.linear(t))) < SURFACE_TOLERANCE

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val t = nearestValidRoot(ray) ?: return false
        sr.t = t
        sr.normal = computeNormal(ray.linear(t))
        return true
    }

    fun hitF(
        ray: Ray,
        sr: Hit,
    ): Boolean {
        val t = nearestValidRoot(ray) ?: return false
        sr.t = t
        sr.normal = computeNormal(ray.linear(t))
        return true
    }

    override fun shadowHit(ray: Ray): Shadow = Shadow.None

    private fun computeNormal(p: Point3D): Normal {
        val paramSquared = a * a + b * b
        val sumSquared = p.x * p.x + p.y * p.y + p.z * p.z
        val diff = sumSquared - paramSquared
        val x = FOUR * p.x * diff
        val y = FOUR * p.y * (diff + TWO * a * a)
        val z = FOUR * p.z * diff
        return Normal(x, y, z).normalize()
    }

    private companion object {
        const val TWO = 2.0
        const val FOUR = 4.0
        const val MAX_ROOTS = 4
        const val SURFACE_TOLERANCE = 1.0E-4
    }
}
