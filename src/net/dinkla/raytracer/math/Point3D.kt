package net.dinkla.raytracer.math

class Point3D : Element3D {

    constructor(x: Float, y: Float, z: Float) : super(x, y, z) {}

    constructor(e: Element3D) : super(e) {}

    operator fun plus(v: Vector3D): Point3D {
        return Point3D(x + v.x, y + v.y, z + v.z)
    }

    operator fun plus(f: Float): Point3D {
        return Point3D(x + f, y + f, z + f)
    }

    operator fun minus(v: Point3D): Vector3D {
        return Vector3D(x - v.x, y - v.y, z - v.z)
    }

    operator fun minus(v: Vector3D): Point3D {
        return Point3D(x - v.x, y - v.y, z - v.z)
    }

    operator fun minus(f: Float): Point3D {
        return Point3D(x - f, y - f, z - f)
    }

    companion object {

        val ORIGIN = Point3D(0f, 0f, 0f)
        val MAX = Point3D(java.lang.Float.POSITIVE_INFINITY, java.lang.Float.POSITIVE_INFINITY, java.lang.Float.POSITIVE_INFINITY)
        val MIN = Point3D(java.lang.Float.NEGATIVE_INFINITY, java.lang.Float.NEGATIVE_INFINITY, java.lang.Float.NEGATIVE_INFINITY)
        val DEFAULT_CAMERA = Point3D(0f, 10f, 10f)
    }

}
