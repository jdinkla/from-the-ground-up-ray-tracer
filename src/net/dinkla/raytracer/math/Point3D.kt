package net.dinkla.raytracer.math

class Point3D : Element3D {

    constructor(x: Double, y: Double, z: Double) : super(x, y, z) {}

    constructor(e: Element3D) : super(e) {}

    operator fun unaryMinus(): Point3D {
        return Point3D(-x, -y, -z)
    }

    operator fun plus(v: Vector3D): Point3D {
        return Point3D(x + v.x, y + v.y, z + v.z)
    }

    operator fun plus(f: Double): Point3D {
        return Point3D(x + f, y + f, z + f)
    }

    operator fun minus(v: Point3D): Vector3D {
        return Vector3D(x - v.x, y - v.y, z - v.z)
    }

    operator fun minus(v: Vector3D): Point3D {
        return Point3D(x - v.x, y - v.y, z - v.z)
    }

    operator fun minus(f: Double): Point3D {
        return Point3D(x - f, y - f, z - f)
    }

    companion object {
        val ORIGIN = Point3D(0.0, 0.0, 0.0)
        val MAX = Point3D(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY)
        val MIN = Point3D(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY)
        val DEFAULT_CAMERA = Point3D(0.0, 10.0, 10.0)
    }

}
