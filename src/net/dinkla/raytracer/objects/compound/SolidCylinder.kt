package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.OpenCylinder

class SolidCylinder(y0: Double, y1: Double, radius: Double) : Compound() {

    init {
        val bottom = Disk(Point3D(0.0, y0, 0.0), radius, Normal(0.0, -1.0, 0.0))
        val top = Disk(Point3D(0.0, y1, 0.0), radius, Normal(0.0, 1.0, 0.0))
        val oc = OpenCylinder(y0, y1, radius)

        objects.add(bottom)
        objects.add(oc)
        objects.add(top)

        boundingBox = BBox(Point3D(-radius, y0, -radius), Point3D(radius, y1, radius))
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        return if (boundingBox.hit(ray)) {
            super.hit(ray, sr)
        } else {
            false
        }
    }
}
