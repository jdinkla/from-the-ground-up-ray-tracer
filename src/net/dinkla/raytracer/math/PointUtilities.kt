package net.dinkla.raytracer.math

import net.dinkla.raytracer.objects.GeometricObject

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 16.05.2010
 * Time: 14:29:33
 * To change this template use File | Settings | File Templates.
 */
object PointUtilities {

    fun minPoints(points: List<Point3D>): Point3D {
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

    fun maxPoints(points: List<Point3D>): Point3D {
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

    fun minCoordinates(objects: List<GeometricObject>): Point3D {
        var x = java.lang.Double.POSITIVE_INFINITY
        var y = java.lang.Double.POSITIVE_INFINITY
        var z = java.lang.Double.POSITIVE_INFINITY
        for (`object` in objects) {
            val bbox = `object`.boundingBox
            if (bbox.p!!.x < x) {
                x = bbox.p.x
            }
            if (bbox.p.y < y) {
                y = bbox.p.y
            }
            if (bbox.p.z < z) {
                z = bbox.p.z
            }
        }
        return Point3D(x - MathUtils.K_EPSILON, y - MathUtils.K_EPSILON, z - MathUtils.K_EPSILON)
    }

    fun maxCoordinates(objects: List<GeometricObject>): Point3D {
        var x = java.lang.Double.NEGATIVE_INFINITY
        var y = java.lang.Double.NEGATIVE_INFINITY
        var z = java.lang.Double.NEGATIVE_INFINITY
        for (`object` in objects) {
            val bbox = `object`.boundingBox
            if (bbox.q!!.x > x) {
                x = bbox.q.x
            }
            if (bbox.q.y > y) {
                y = bbox.q.y
            }
            if (bbox.q.z > z) {
                z = bbox.q.z
            }
        }
        return Point3D(x + MathUtils.K_EPSILON, y + MathUtils.K_EPSILON, z + MathUtils.K_EPSILON)
    }

}
