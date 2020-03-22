package net.dinkla.raytracer.math

import net.dinkla.raytracer.objects.GeometricObject

// TODO simplify and unify
object PointUtilities {

    fun minimum(v: Array<Point3D>, n: Int): Triple<Double, Double, Double> {
        var x0 = MathUtils.K_HUGEVALUE
        var y0 = MathUtils.K_HUGEVALUE
        var z0 = MathUtils.K_HUGEVALUE

        for (j in 0 until n) {
            if (v[j].x < x0) {
                x0 = v[j].x
            }
            if (v[j].y < y0) {
                y0 = v[j].y
            }
            if (v[j].z < z0) {
                z0 = v[j].z
            }
        }
        return Triple(x0, y0, z0)
    }

    fun maximum(v: Array<Point3D>, n: Int): Triple<Double, Double, Double> {
        var x1 = -MathUtils.K_HUGEVALUE
        var y1 = -MathUtils.K_HUGEVALUE
        var z1 = -MathUtils.K_HUGEVALUE

        for (j in 0 until n) {
            if (v[j].x > x1) {
                x1 = v[j].x
            }
            if (v[j].y > y1) {
                y1 = v[j].y
            }
            if (v[j].z > z1) {
                z1 = v[j].z
            }
        }

        return Triple(x1, y1, z1)
    }

    fun minPoints(points: ArrayList<Point3D>): Point3D {
        var x = java.lang.Double.POSITIVE_INFINITY
        var y = java.lang.Double.POSITIVE_INFINITY
        var z = java.lang.Double.POSITIVE_INFINITY
        for (p in points) {
            if (p.x < x) {
                x = p.x
            }
            if (p.y < y) {
                y = p.y
            }
            if (p.z < z) {
                z = p.z
            }
        }
        return Point3D(x, y, z)
    }

    fun maxPoints(points: ArrayList<Point3D>): Point3D {
        var x = java.lang.Double.NEGATIVE_INFINITY
        var y = java.lang.Double.NEGATIVE_INFINITY
        var z = java.lang.Double.NEGATIVE_INFINITY
        for (p in points) {
            if (p.x > x) {
                x = p.x
            }
            if (p.y > y) {
                y = p.y
            }
            if (p.z > z) {
                z = p.z
            }
        }
        return Point3D(x, y, z)
    }

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


}
