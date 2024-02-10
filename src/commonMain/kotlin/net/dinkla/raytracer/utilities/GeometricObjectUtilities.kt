package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.IGeometricObject

object GeometricObjectUtilities {
    fun minMaxCoordinates(objects: ArrayList<IGeometricObject>): Pair<Point3D, Point3D> {
        var minX = Double.POSITIVE_INFINITY
        var minY = Double.POSITIVE_INFINITY
        var minZ = Double.POSITIVE_INFINITY
        var maxX = Double.NEGATIVE_INFINITY
        var maxY = Double.NEGATIVE_INFINITY
        var maxZ = Double.NEGATIVE_INFINITY
        for (geometricObject in objects) {
            val bbox = geometricObject.boundingBox
            if (bbox.p.x < minX) {
                minX = bbox.p.x
            }
            if (bbox.p.y < minY) {
                minY = bbox.p.y
            }
            if (bbox.p.z < minZ) {
                minZ = bbox.p.z
            }
            if (bbox.q.x > maxX) {
                maxX = bbox.q.x
            }
            if (bbox.q.y > maxY) {
                maxY = bbox.q.y
            }
            if (bbox.q.z > maxZ) {
                maxZ = bbox.q.z
            }
        }
        return Pair(
            Point3D(minX - MathUtils.K_EPSILON, minY - MathUtils.K_EPSILON, minZ - MathUtils.K_EPSILON),
            Point3D(maxX + MathUtils.K_EPSILON, maxY + MathUtils.K_EPSILON, maxZ + MathUtils.K_EPSILON)
        )
    }

    fun create(objects: ArrayList<IGeometricObject>): BBox {
        if (objects.size > 0) {
            val (p0, p1) = minMaxCoordinates(objects)
            return BBox(p0, p1)
        } else {
            return BBox()
        }
    }
}
