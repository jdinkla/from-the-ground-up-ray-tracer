package net.dinkla.raytracer.objects.beveled

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.objects.Torus
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.equals
import java.util.Objects

/**
 * A solid cylinder (axis on +y, spanning `y = `[y0]`..`[y1], radius [radius]) with its top and bottom
 * edges rounded by a bevel of radius [rb]. It is assembled from five parts:
 *  - the **body**: an [OpenCylinder] of full [radius] shortened to `y0 + rb .. y1 - rb` so the rounded
 *    rims take over at each end,
 *  - the **caps**: two flat [Disk]s of radius `radius - rb` closing the ends at [y0] and [y1],
 *  - the **rims**: a [Torus] of sweep radius `radius - rb` and tube radius [rb] at each end, translated
 *    via an [Instance] to `y = y0 + rb` (bottom) and `y = y1 - rb` (top) so its tube rounds the edge.
 *
 * Analogous to [BeveledBox] (which rounds a box's edges with cylinders, tori-free) and to
 * [net.dinkla.raytracer.objects.compound.SolidCylinder] (the un-beveled solid cylinder); see Suffern,
 * *Ray Tracing from the Ground Up*, ch. 21 (the `BeveledCylinder` part object).
 */
class BeveledCylinder(
    val y0: Double,
    val y1: Double,
    val radius: Double,
    val rb: Double,
) : Compound() {
    init {
        val bottomCap = Disk(Point3D(0.0, y0, 0.0), radius - rb, Normal.DOWN)
        val topCap = Disk(Point3D(0.0, y1, 0.0), radius - rb, Normal.UP)
        val body = OpenCylinder(y0 + rb, y1 - rb, radius)

        objects.add(bottomCap)
        objects.add(topCap)
        objects.add(body)
        objects.add(rim(y0 + rb))
        objects.add(rim(y1 - rb))

        boundingBox = BBox(Point3D(-radius, y0, -radius), Point3D(radius, y1, radius))
    }

    /** A torus rim (sweep radius `radius - rb`, tube radius [rb]) translated to height [y]. */
    private fun rim(y: Double): Instance {
        val torus = Instance(Torus(radius - rb, rb))
        torus.translate(Vector3D(0.0, y, 0.0))
        return torus
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
        this.equals<BeveledCylinder>(other) { a, b ->
            a.y0 == b.y0 && a.y1 == b.y1 && a.radius == b.radius && a.rb == b.rb
        }

    override fun hashCode(): Int = Objects.hash(y0, y1, radius, rb)

    override fun toString(): String = "BeveledCylinder($y0, $y1, $radius, $rb)"
}
