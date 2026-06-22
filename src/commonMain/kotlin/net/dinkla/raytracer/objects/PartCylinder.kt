package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.equals
import java.util.Objects
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * An [OpenCylinder] (lateral surface only, axis on +y) restricted to an azimuth wedge
 * `[phiMin, phiMax]` (radians) in addition to its y-extent `[y0, y1]`. A full open cylinder
 * corresponds to `phi 0..2π`.
 *
 * Analogous to [OpenCylinder]; see Suffern, *Ray Tracing from the Ground Up*, ch. 19.
 */
class PartCylinder(
    y0: Double,
    y1: Double,
    private val radius: Double,
    private val phiMin: Double = 0.0,
    private val phiMax: Double = PartAngles.TWO_PI,
) : GeometricObject() {
    val y0: Double = min(y0, y1)
    val y1: Double = max(y0, y1)
    private val invRadius: Double = 1.0 / radius

    init {
        boundingBox =
            BBox(
                Point3D(-radius - MathUtils.K_EPSILON, this.y0, -radius - MathUtils.K_EPSILON),
                Point3D(radius + MathUtils.K_EPSILON, this.y1, radius + MathUtils.K_EPSILON),
            )
    }

    private fun isInExtent(
        x: Double,
        y: Double,
        z: Double,
    ): Boolean {
        if (y <= y0 || y >= y1) {
            return false
        }
        return PartAngles.inPhiRange(PartAngles.phi(x, z), phiMin, phiMax)
    }

    private fun outwardNormal(
        ray: Ray,
        x: Double,
        z: Double,
    ): Normal {
        var n = Normal(x * invRadius, 0.0, z * invRadius)
        if (ray.direction.times(-1.0).dot(n) < 0.0) {
            n = Normal.create(n.times(-1.0))
        }
        return n
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val (t1, t2) = roots(ray) ?: return false
        return when {
            isValid(t1, ray) -> accept(t1, ray, sr)
            isValid(t2, ray) -> accept(t2, ray, sr)
            else -> false
        }
    }

    override fun shadowHit(ray: Ray): Shadow {
        val (t1, t2) = roots(ray) ?: return Shadow.None
        return when {
            isValid(t1, ray) -> Shadow.Hit(t1)
            isValid(t2, ray) -> Shadow.Hit(t2)
            else -> Shadow.None
        }
    }

    /** The two real roots of the ray/cylinder quadratic ordered nearest-first, or `null` when none. */
    private fun roots(ray: Ray): Pair<Double, Double>? {
        val ox = ray.origin.x
        val oz = ray.origin.z
        val dx = ray.direction.x
        val dz = ray.direction.z

        val a = dx * dx + dz * dz
        val b = 2.0 * (ox * dx + oz * dz)
        val c = ox * ox + oz * oz - radius * radius
        val disc = b * b - 4.0 * a * c
        if (disc < 0.0) {
            return null
        }
        val e = sqrt(disc)
        val denom = 2.0 * a
        return (-b - e) / denom to (-b + e) / denom
    }

    /** True when [t] is a forward intersection inside the cylinder's extent and azimuth wedge. */
    private fun isValid(
        t: Double,
        ray: Ray,
    ): Boolean {
        if (t <= MathUtils.K_EPSILON) {
            return false
        }
        val p = ray.linear(t)
        return isInExtent(p.x, p.y, p.z)
    }

    /** Records the hit at [t] into [sr] and returns true; a single-expression helper for `hit`. */
    private fun accept(
        t: Double,
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val p = ray.linear(t)
        sr.t = t
        sr.normal = outwardNormal(ray, p.x, p.z)
        return true
    }

    override fun equals(other: Any?): Boolean =
        this.equals<PartCylinder>(other) { a, b ->
            a.y0 == b.y0 &&
                a.y1 == b.y1 &&
                a.radius == b.radius &&
                a.phiMin == b.phiMin &&
                a.phiMax == b.phiMax
        }

    override fun hashCode(): Int = Objects.hash(y0, y1, radius, phiMin, phiMax)

    override fun toString(): String = "PartCylinder($y0, $y1, $radius, phi=[$phiMin,$phiMax])"
}
