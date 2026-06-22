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

/**
 * A flat ring: a [Disk] of outer radius [outerRadius] centred at [center] facing [normal], with a
 * concentric circular hole of inner radius [innerRadius] removed. A hit is accepted only when the
 * point lies in the annular band `innerRadius <= |p - center| <= outerRadius`.
 *
 * Analogous to [Disk]; see Suffern, *Ray Tracing from the Ground Up*, ch. 19.
 */
class Annulus(
    val center: Point3D,
    val innerRadius: Double,
    val outerRadius: Double,
    val normal: Normal,
) : GeometricObject() {
    private val innerSquared = innerRadius * innerRadius
    private val outerSquared = outerRadius * outerRadius

    init {
        boundingBox = BBox(center - outerRadius, center + outerRadius)
    }

    private fun isInBand(p: Point3D): Boolean {
        val d = center.sqrDistance(p)
        return d in innerSquared..outerSquared
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val t = ((center - ray.origin) dot normal) / (ray.direction dot normal)
        if (t <= MathUtils.K_EPSILON) {
            return false
        }
        return if (isInBand(ray.linear(t))) {
            sr.t = t
            sr.normal = normal
            true
        } else {
            false
        }
    }

    override fun shadowHit(ray: Ray): Shadow {
        val t = ((center - ray.origin) dot normal) / (ray.direction dot normal)
        return when {
            t <= MathUtils.K_EPSILON -> Shadow.None
            isInBand(ray.linear(t)) -> Shadow.Hit(t)
            else -> Shadow.None
        }
    }

    override fun equals(other: Any?): Boolean =
        this.equals<Annulus>(other) { a, b ->
            a.center == b.center &&
                a.innerRadius == b.innerRadius &&
                a.outerRadius == b.outerRadius &&
                a.normal == b.normal
        }

    override fun hashCode(): Int = Objects.hash(center, innerRadius, outerRadius, normal)

    override fun toString(): String = "Annulus($center, $innerRadius, $outerRadius, $normal)"
}
