package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.Annulus
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.utilities.equals
import java.util.Objects

/**
 * A thick ring (a tube with a square cross-section): the solid region between two coaxial cylinders
 * of radii [innerRadius] and [outerRadius], capped top and bottom. It is assembled from four parts —
 * an outer [OpenCylinder] wall, an inner [OpenCylinder] wall, and two flat [Annulus] rims closing the
 * gap at `y = `[y0] and `y = `[y1].
 *
 * Analogous to [SolidCylinder] (which has no hole); see Suffern, *Ray Tracing from the Ground Up*,
 * ch. 21 (the `ThickRing` part object).
 */
class ThickRing(
    val y0: Double,
    val y1: Double,
    val innerRadius: Double,
    val outerRadius: Double,
) : Compound() {
    init {
        val bottom = Annulus(Point3D(0.0, y0, 0.0), innerRadius, outerRadius, Normal.DOWN)
        val top = Annulus(Point3D(0.0, y1, 0.0), innerRadius, outerRadius, Normal.UP)
        val outerWall = OpenCylinder(y0, y1, outerRadius)
        val innerWall = OpenCylinder(y0, y1, innerRadius)

        objects.add(bottom)
        objects.add(top)
        objects.add(outerWall)
        objects.add(innerWall)

        boundingBox =
            BBox(
                Point3D(-outerRadius - MathUtils.K_EPSILON, y0, -outerRadius - MathUtils.K_EPSILON),
                Point3D(outerRadius + MathUtils.K_EPSILON, y1, outerRadius + MathUtils.K_EPSILON),
            )
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean =
        if (boundingBox.isHit(ray)) {
            super.hit(ray, sr)
        } else {
            false
        }

    override fun equals(other: Any?): Boolean =
        this.equals<ThickRing>(other) { a, b ->
            a.y0 == b.y0 &&
                a.y1 == b.y1 &&
                a.innerRadius == b.innerRadius &&
                a.outerRadius == b.outerRadius
        }

    override fun hashCode(): Int = Objects.hash(y0, y1, innerRadius, outerRadius)

    override fun toString(): String = "ThickRing($y0, $y1, $innerRadius, $outerRadius)"
}
