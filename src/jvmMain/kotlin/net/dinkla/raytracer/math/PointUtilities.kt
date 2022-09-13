package net.dinkla.raytracer.math

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
}
