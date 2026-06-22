package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.equals
import java.util.Objects
import kotlin.math.sqrt

/**
 * A sphere whose surface normal points **inward**, toward the [center]. It is the same quadratic
 * surface as [Sphere], but the normal is negated so the *inside* of the sphere is the lit surface.
 * This is what environment/skylight domes need: the camera and the scene sit inside the sphere and
 * rays strike its interior wall, so the normal must face back toward the center for correct shading.
 *
 * The only difference from [Sphere] is the normal direction; the intersection math is identical.
 * See Suffern, *Ray Tracing from the Ground Up*, ch. 29 (environment lighting).
 */
class ConcaveSphere(
    val center: Point3D = Point3D.ORIGIN,
    val radius: Double = 1.0,
) : GeometricObject() {
    init {
        boundingBox = BBox(center - radius, center + radius)
    }

    constructor(center: Point3D, radius: Double, material: IMaterial) : this(center, radius) {
        this.material = material
    }

    /** The inward-facing normal at the hit point reached at distance [t] from [temp] = origin − center. */
    private fun inwardNormal(
        ray: Ray,
        t: Double,
        temp: Vector3D,
    ): Normal {
        // (origin + t·d − center) / radius is the outward normal; negate it so it points inward.
        val outward = (ray.direction * t + temp) * (1.0 / radius)
        return Normal.create(outward * -1.0)
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val temp = ray.origin - center
        val (t1, t2) = roots(ray, temp) ?: return false
        return when {
            t1 > MathUtils.K_EPSILON -> accept(t1, ray, temp, sr)
            t2 > MathUtils.K_EPSILON -> accept(t2, ray, temp, sr)
            else -> false
        }
    }

    override fun shadowHit(ray: Ray): Shadow {
        val temp = ray.origin - center
        val (t1, t2) = roots(ray, temp) ?: return Shadow.None
        return when {
            t1 > MathUtils.K_EPSILON -> Shadow.Hit(t1)
            t2 > MathUtils.K_EPSILON -> Shadow.Hit(t2)
            else -> Shadow.None
        }
    }

    /** The two real roots of the ray/sphere quadratic ordered nearest-first, or `null` when none. */
    private fun roots(
        ray: Ray,
        temp: Vector3D,
    ): Pair<Double, Double>? {
        val a = ray.direction dot ray.direction
        val b = (temp * 2.0) dot ray.direction
        val c = (temp dot temp) - radius * radius
        val disc = b * b - 4.0 * a * c
        if (disc < 0.0) {
            return null
        }
        val e = sqrt(disc)
        val denom = 2.0 * a
        return (-b - e) / denom to (-b + e) / denom
    }

    /** Records the hit at [t] into [sr] with the inward normal and returns true. */
    private fun accept(
        t: Double,
        ray: Ray,
        temp: Vector3D,
        sr: IHit,
    ): Boolean {
        sr.t = t
        sr.normal = inwardNormal(ray, t, temp)
        return true
    }

    override fun equals(other: Any?): Boolean =
        this.equals<ConcaveSphere>(other) { a, b ->
            a.center == b.center && a.radius == b.radius && a.material == b.material
        }

    override fun hashCode(): Int = Objects.hash(center, radius)

    override fun toString(): String = "ConcaveSphere($center, $radius)"
}
