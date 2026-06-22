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
import kotlin.math.sqrt

/**
 * An open (lateral surface only, no base cap) cone with its axis on +y, base circle of radius
 * [radius] in the plane `y = 0` and apex at `(0, height, 0)`. The surface satisfies
 * `x² + z² = (radius/height)² · (height − y)²` for `0 ≤ y ≤ height`.
 *
 * Analogous to [OpenCylinder]; see Suffern, *Ray Tracing from the Ground Up*, ch. 19.
 */
class OpenCone(
    val height: Double,
    val radius: Double,
) : GeometricObject() {
    // k = (radius / height)^2 is the squared slope relating the radial distance to the height below
    // the apex; tanSquared in Suffern's derivation.
    private val k: Double = (radius / height) * (radius / height)

    init {
        boundingBox =
            BBox(
                Point3D(-radius - MathUtils.K_EPSILON, 0.0, -radius - MathUtils.K_EPSILON),
                Point3D(radius + MathUtils.K_EPSILON, height + MathUtils.K_EPSILON, radius + MathUtils.K_EPSILON),
            )
    }

    private fun isInExtent(y: Double): Boolean = y in 0.0..height

    private fun outwardNormal(
        ray: Ray,
        p: Point3D,
    ): Normal {
        var n = Normal(p.x, k * (height - p.y), p.z).normalize()
        if (ray.direction.times(-1.0).dot(n) < 0.0) {
            n = Normal.create(n.times(-1.0))
        }
        return n
    }

    /** Quadratic coefficients `A·t² + B·t + C` of the ray/cone intersection. */
    private fun coeffs(ray: Ray): Triple<Double, Double, Double> {
        val ox = ray.origin.x
        val oy = ray.origin.y
        val oz = ray.origin.z
        val dx = ray.direction.x
        val dy = ray.direction.y
        val dz = ray.direction.z
        val g = height - oy

        val a = dx * dx + dz * dz - k * dy * dy
        val b = 2.0 * (ox * dx + oz * dz) + 2.0 * k * g * dy
        val c = ox * ox + oz * oz - k * g * g
        return Triple(a, b, c)
    }

    /** The two real roots of the ray/cone quadratic ordered nearest-first, or `null` when there are none. */
    private fun roots(ray: Ray): Pair<Double, Double>? {
        val (a, b, c) = coeffs(ray)
        val disc = b * b - 4.0 * a * c
        if (a == 0.0 || disc < 0.0) {
            return null
        }
        val e = sqrt(disc)
        val denom = 2.0 * a
        return (-b - e) / denom to (-b + e) / denom
    }

    /** True when [t] is a forward intersection inside the cone's y-extent. */
    private fun isValid(
        t: Double,
        ray: Ray,
    ): Boolean = t > MathUtils.K_EPSILON && isInExtent(ray.linear(t).y)

    /** Records the hit at [t] into [sr] and returns true; a single-expression helper for `hit`. */
    private fun accept(
        t: Double,
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val p = ray.linear(t)
        sr.t = t
        sr.normal = outwardNormal(ray, p)
        return true
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

    override fun equals(other: Any?): Boolean =
        this.equals<OpenCone>(other) { p, q ->
            p.height == q.height && p.radius == q.radius
        }

    override fun hashCode(): Int = Objects.hash(height, radius)

    override fun toString(): String = "OpenCone($height, $radius)"
}
