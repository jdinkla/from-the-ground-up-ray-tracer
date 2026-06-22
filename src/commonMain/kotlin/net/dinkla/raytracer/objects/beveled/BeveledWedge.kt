package net.dinkla.raytracer.objects.beveled

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.PartAnnulus
import net.dinkla.raytracer.objects.PartCylinder
import net.dinkla.raytracer.objects.PartTorus
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.equals
import java.util.Objects
import kotlin.math.cos
import kotlin.math.sin

/**
 * A wedge (angular sector) of a thick cylindrical tube, with its curved top and bottom edges rounded
 * by a bevel of radius [rb]. The wedge spans the azimuth `[phiMin, phiMax]` (radians, `phi = atan2(x, z)`),
 * the height `y = `[y0]`..`[y1], and the radial band [innerRadius]..[outerRadius]. It is assembled from:
 *  - the **outer and inner curved walls**: two [PartCylinder]s shortened to `y0 + rb .. y1 - rb`,
 *  - the **flat top and bottom caps**: two [PartAnnulus]es narrowed to `innerRadius + rb .. outerRadius - rb`,
 *  - the **rounded arc rims**: four [PartTorus] bevels (outer/inner × top/bottom), each translated via an
 *    [Instance] to its corner height, rounding the curved top/bottom edges,
 *  - the **two flat radial sides**: [Rectangle]s closing the wedge at `phi = `[phiMin] and [phiMax].
 *
 * The two straight radial edges are left square (not rounded); only the curved arc edges are beveled.
 * This is the dominant, book-relevant bevel and keeps the part-matching exact (cf. [BeveledCylinder]).
 *
 * Analogous to [BeveledCylinder] / [BeveledBox]; see Suffern, *Ray Tracing from the Ground Up*, ch. 21
 * (the `BeveledWedge` part object).
 */
class BeveledWedge(
    val y0: Double,
    val y1: Double,
    val innerRadius: Double,
    val outerRadius: Double,
    val phiMin: Double,
    val phiMax: Double,
    val rb: Double,
) : Compound() {
    init {
        val innerWall = innerRadius + rb
        val outerWall = outerRadius - rb

        objects.add(PartCylinder(y0 + rb, y1 - rb, outerRadius, phiMin, phiMax))
        objects.add(PartCylinder(y0 + rb, y1 - rb, innerRadius, phiMin, phiMax))
        objects.add(PartAnnulus(Point3D(0.0, y1, 0.0), innerWall, outerWall, Normal.UP, phiMin, phiMax))
        objects.add(PartAnnulus(Point3D(0.0, y0, 0.0), innerWall, outerWall, Normal.DOWN, phiMin, phiMax))

        objects.add(rim(outerWall, y1 - rb))
        objects.add(rim(outerWall, y0 + rb))
        objects.add(rim(innerWall, y1 - rb))
        objects.add(rim(innerWall, y0 + rb))

        objects.add(radialSide(phiMin))
        objects.add(radialSide(phiMax))

        boundingBox =
            BBox(
                Point3D(-outerRadius, y0, -outerRadius),
                Point3D(outerRadius, y1, outerRadius),
            )
    }

    /** A [PartTorus] arc rim (sweep radius [sweep], tube radius [rb]) translated to height [y]. */
    private fun rim(
        sweep: Double,
        y: Double,
    ): Instance {
        val torus = Instance(PartTorus(sweep, rb, phiMin, phiMax))
        torus.translate(Vector3D(0.0, y, 0.0))
        return torus
    }

    /** A flat radial [Rectangle] closing the wedge at azimuth [phi], spanning the full radial band and height. */
    private fun radialSide(phi: Double): Rectangle {
        val radial = Vector3D(sin(phi), 0.0, cos(phi))
        val corner = Point3D(innerRadius * radial.x, y0, innerRadius * radial.z)
        val along = radial * (outerRadius - innerRadius)
        val up = Vector3D(0.0, y1 - y0, 0.0)
        return Rectangle(corner, along, up)
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
        this.equals<BeveledWedge>(other) { a, b ->
            a.y0 == b.y0 &&
                a.y1 == b.y1 &&
                a.innerRadius == b.innerRadius &&
                a.outerRadius == b.outerRadius &&
                a.phiMin == b.phiMin &&
                a.phiMax == b.phiMax &&
                a.rb == b.rb
        }

    override fun hashCode(): Int = Objects.hash(y0, y1, innerRadius, outerRadius, phiMin, phiMax, rb)

    override fun toString(): String =
        "BeveledWedge($y0, $y1, $innerRadius, $outerRadius, phi=[$phiMin,$phiMax], $rb)"
}
