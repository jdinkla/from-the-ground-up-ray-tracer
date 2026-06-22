package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.OpenCone
import net.dinkla.raytracer.utilities.equals
import java.util.Objects

/**
 * A closed (solid) cone: an [OpenCone] lateral surface of the given [height] and base [radius]
 * (axis on +y, base at `y = 0`, apex at `(0, height, 0)`) capped by a base [Disk] facing −y.
 *
 * Analogous to [SolidCylinder]; see Suffern, *Ray Tracing from the Ground Up*, ch. 19.
 */
class SolidCone(
    val height: Double,
    val radius: Double,
) : Compound() {
    init {
        val base = Disk(Point3D(0.0, 0.0, 0.0), radius, Normal(0.0, -1.0, 0.0))
        val lateral = OpenCone(height, radius)

        objects.add(base)
        objects.add(lateral)

        boundingBox =
            BBox(
                Point3D(-radius - MathUtils.K_EPSILON, 0.0, -radius - MathUtils.K_EPSILON),
                Point3D(radius + MathUtils.K_EPSILON, height + MathUtils.K_EPSILON, radius + MathUtils.K_EPSILON),
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
        this.equals<SolidCone>(other) { a, b ->
            a.height == b.height && a.radius == b.radius
        }

    override fun hashCode(): Int = Objects.hash(height, radius)

    override fun toString(): String = "SolidCone($height, $radius)"
}
