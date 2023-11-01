package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.utilities.hash

class SolidCylinder(val y0: Double, val y1: Double, val radius: Double) : Compound() {

    init {
        val bottom = Disk(Point3D(0.0, y0, 0.0), radius, Normal(0.0, -1.0, 0.0))
        val top = Disk(Point3D(0.0, y1, 0.0), radius, Normal(0.0, 1.0, 0.0))
        val oc = OpenCylinder(y0, y1, radius)

        objects.add(bottom)
        objects.add(oc)
        objects.add(top)

        boundingBox = BBox(Point3D(-radius, y0, -radius), Point3D(radius, y1, radius))
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
        return if (boundingBox.hit(ray)) {
            super.hit(ray, sr)
        } else {
            false
        }
    }

    override fun equals(other: Any?): Boolean = this.equals<SolidCylinder>(other) { a, b ->
        a.y0 == b.y0 && a.y1 == b.y1 && a.radius == b.radius
    }

    override fun hashCode(): Int = this.hash(y0, y1, radius)

    override fun toString(): String = "SolidCylinder($y0, $y1, $radius)"
}
