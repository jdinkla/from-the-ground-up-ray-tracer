package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.Annulus
import net.dinkla.raytracer.objects.ConcavePartSphere
import net.dinkla.raytracer.objects.PartSphere
import net.dinkla.raytracer.utilities.equals
import java.util.Objects

/**
 * A bowl: a thick hemispherical shell centred at the origin, opening upward. It is the solid region
 * between an inner sphere of radius [innerRadius] and an outer sphere of radius [outerRadius],
 * restricted to the lower hemisphere (`y ≤ 0`). It is assembled from three parts:
 *  - the **outer wall**: the lower hemisphere of the outer sphere ([PartSphere], convex / outward
 *    normals — you see it from outside the bowl),
 *  - the **inner wall**: the lower hemisphere of the inner sphere ([ConcavePartSphere], concave /
 *    inward normals — you see its inside, looking down into the bowl),
 *  - the **rim**: a flat [Annulus] at the equator `y = 0` closing the gap between the two walls.
 *
 * The polar band kept is `theta ∈ [π/2, π]` (theta measured from +y), i.e. the bottom half.
 *
 * See Suffern, *Ray Tracing from the Ground Up*, ch. 19 (the `Bowl` part object).
 */
class Bowl(
    val innerRadius: Double,
    val outerRadius: Double,
) : Compound() {
    init {
        val outerWall =
            PartSphere(
                center = Point3D.ORIGIN,
                radius = outerRadius,
                thetaMin = MathUtils.PI / 2.0,
                thetaMax = MathUtils.PI,
            )
        val innerWall =
            ConcavePartSphere(
                center = Point3D.ORIGIN,
                radius = innerRadius,
                thetaMin = MathUtils.PI / 2.0,
                thetaMax = MathUtils.PI,
            )
        val rim = Annulus(Point3D.ORIGIN, innerRadius, outerRadius, Normal.UP)

        objects.add(outerWall)
        objects.add(innerWall)
        objects.add(rim)

        val eps = MathUtils.K_EPSILON
        boundingBox =
            BBox(
                Point3D(-outerRadius - eps, -outerRadius - eps, -outerRadius - eps),
                Point3D(outerRadius + eps, eps, outerRadius + eps),
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
        this.equals<Bowl>(other) { a, b ->
            a.innerRadius == b.innerRadius && a.outerRadius == b.outerRadius
        }

    override fun hashCode(): Int = Objects.hash(innerRadius, outerRadius)

    override fun toString(): String = "Bowl($innerRadius, $outerRadius)"
}
