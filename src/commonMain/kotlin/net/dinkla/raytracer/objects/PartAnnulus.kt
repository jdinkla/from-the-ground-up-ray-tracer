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
 * An [Annulus] (flat ring centred at [center], facing [normal], radii [innerRadius]..[outerRadius])
 * restricted to an azimuth wedge `[phiMin, phiMax]` (radians) around the y-axis. A full annulus
 * corresponds to `phi 0..2π`. Used as the flat top/bottom caps of a
 * [net.dinkla.raytracer.objects.beveled.BeveledWedge].
 *
 * Analogous to [Annulus]; the wedge rejection reuses [PartAngles] like [PartCylinder] / [PartTorus].
 * See Suffern, *Ray Tracing from the Ground Up*, ch. 21.
 */
class PartAnnulus(
    val center: Point3D,
    val innerRadius: Double,
    val outerRadius: Double,
    val normal: Normal,
    val phiMin: Double = 0.0,
    val phiMax: Double = PartAngles.TWO_PI,
) : GeometricObject() {
    private val innerSquared = innerRadius * innerRadius
    private val outerSquared = outerRadius * outerRadius

    init {
        boundingBox = BBox(center - outerRadius, center + outerRadius)
    }

    private fun isInBandAndWedge(p: Point3D): Boolean {
        val d = center.sqrDistance(p)
        if (d !in innerSquared..outerSquared) {
            return false
        }
        val phi = PartAngles.phi(p.x - center.x, p.z - center.z)
        return PartAngles.inPhiRange(phi, phiMin, phiMax)
    }

    private fun distance(ray: Ray): Double = ((center - ray.origin) dot normal) / (ray.direction dot normal)

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val t = distance(ray)
        if (t <= MathUtils.K_EPSILON) {
            return false
        }
        return if (isInBandAndWedge(ray.linear(t))) {
            sr.t = t
            sr.normal = normal
            true
        } else {
            false
        }
    }

    override fun shadowHit(ray: Ray): Shadow {
        val t = distance(ray)
        return when {
            t <= MathUtils.K_EPSILON -> Shadow.None
            isInBandAndWedge(ray.linear(t)) -> Shadow.Hit(t)
            else -> Shadow.None
        }
    }

    override fun equals(other: Any?): Boolean =
        this.equals<PartAnnulus>(other) { a, b ->
            a.center == b.center &&
                a.innerRadius == b.innerRadius &&
                a.outerRadius == b.outerRadius &&
                a.normal == b.normal &&
                a.phiMin == b.phiMin &&
                a.phiMax == b.phiMax
        }

    override fun hashCode(): Int = Objects.hash(center, innerRadius, outerRadius, normal, phiMin, phiMax)

    override fun toString(): String =
        "PartAnnulus($center, $innerRadius, $outerRadius, $normal, phi=[$phiMin,$phiMax])"
}
