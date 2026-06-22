package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
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
 * A [Sphere] restricted to an angular wedge: only the surface whose azimuth (phi) lies in
 * `[phiMin, phiMax]` and whose polar angle (theta, from +y) lies in `[thetaMin, thetaMax]` is kept.
 * All four limits are in radians. A full sphere corresponds to `phi 0..2π`, `theta 0..π`.
 *
 * Analogous to [Sphere]; see Suffern, *Ray Tracing from the Ground Up*, ch. 19.
 */
class PartSphere(
    val center: Point3D = Point3D.ORIGIN,
    val radius: Double = 1.0,
    val phiMin: Double = 0.0,
    val phiMax: Double = PartAngles.TWO_PI,
    val thetaMin: Double = 0.0,
    val thetaMax: Double = MathUtils.PI,
) : GeometricObject() {
    init {
        boundingBox = BBox(center - radius, center + radius)
    }

    private fun isInWedge(p: Point3D): Boolean {
        val lx = p.x - center.x
        val ly = p.y - center.y
        val lz = p.z - center.z
        val phi = PartAngles.phi(lx, lz)
        val theta = PartAngles.theta(ly, radius)
        return PartAngles.inPhiRange(phi, phiMin, phiMax) &&
            PartAngles.inThetaRange(theta, thetaMin, thetaMax)
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val temp = ray.origin - center
        val (t1, t2) = roots(ray, temp) ?: return false
        return when {
            isValid(t1, ray) -> accept(t1, ray, temp, sr)
            isValid(t2, ray) -> accept(t2, ray, temp, sr)
            else -> false
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

    /** True when [t] is a forward intersection whose hit point lies in the kept wedge. */
    private fun isValid(
        t: Double,
        ray: Ray,
    ): Boolean = t > MathUtils.K_EPSILON && isInWedge(ray.linear(t))

    /** Records the hit at [t] into [sr] and returns true; a single-expression helper for `hit`. */
    private fun accept(
        t: Double,
        ray: Ray,
        temp: Vector3D,
        sr: IHit,
    ): Boolean {
        sr.t = t
        sr.normal = Normal.create((ray.direction * t + temp) * (1.0 / radius))
        return true
    }

    override fun shadowHit(ray: Ray): Shadow {
        val temp = ray.origin - center
        val (t1, t2) = roots(ray, temp) ?: return Shadow.None
        return when {
            isValid(t1, ray) -> Shadow.Hit(t1)
            isValid(t2, ray) -> Shadow.Hit(t2)
            else -> Shadow.None
        }
    }

    override fun equals(other: Any?): Boolean =
        this.equals<PartSphere>(other) { a, b ->
            a.center == b.center &&
                a.radius == b.radius &&
                a.phiMin == b.phiMin &&
                a.phiMax == b.phiMax &&
                a.thetaMin == b.thetaMin &&
                a.thetaMax == b.thetaMax
        }

    override fun hashCode(): Int = Objects.hash(center, radius, phiMin, phiMax, thetaMin, thetaMax)

    override fun toString(): String =
        "PartSphere($center, $radius, phi=[$phiMin,$phiMax], theta=[$thetaMin,$thetaMax])"
}
