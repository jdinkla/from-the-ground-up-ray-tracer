package net.dinkla.raytracer.math

import net.dinkla.raytracer.objects.GeometricObject

object GeometricObjectUtilities {

    fun minCoordinates(objects: ArrayList<GeometricObject>): Point3D {
        var minX = java.lang.Double.POSITIVE_INFINITY
        var minY = java.lang.Double.POSITIVE_INFINITY
        var minZ = java.lang.Double.POSITIVE_INFINITY
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
        }
        return Point3D(minX - MathUtils.K_EPSILON, minY - MathUtils.K_EPSILON, minZ - MathUtils.K_EPSILON)
    }

    fun maxCoordinates(objects: ArrayList<GeometricObject>): Point3D {
        var maxX = java.lang.Double.NEGATIVE_INFINITY
        var maxY = java.lang.Double.NEGATIVE_INFINITY
        var maxZ = java.lang.Double.NEGATIVE_INFINITY
        for (geometricObject in objects) {
            val bbox = geometricObject.boundingBox
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
        return Point3D(maxX + MathUtils.K_EPSILON, maxY + MathUtils.K_EPSILON, maxZ + MathUtils.K_EPSILON)
    }

    fun minMaxCoordinates(objects: ArrayList<GeometricObject>): Pair<Point3D, Point3D> {
        var minX = java.lang.Double.POSITIVE_INFINITY
        var minY = java.lang.Double.POSITIVE_INFINITY
        var minZ = java.lang.Double.POSITIVE_INFINITY
        var maxX = java.lang.Double.NEGATIVE_INFINITY
        var maxY = java.lang.Double.NEGATIVE_INFINITY
        var maxZ = java.lang.Double.NEGATIVE_INFINITY
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

    fun create(objects: ArrayList<GeometricObject>): BBox {
        if (objects.size > 0) {
            val (p0, p1) = minMaxCoordinates(objects)
            return BBox(p0, p1)
        } else {
            return BBox()
        }
    }

}