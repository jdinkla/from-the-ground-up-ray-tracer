package net.dinkla.raytracer.math

data class Point3D(
    val x: Double,
    val y: Double,
    val z: Double,
) {
    operator fun unaryMinus() = Vector3D(-x, -y, -z)

    operator fun plus(v: Vector3D) = Point3D(x + v.x, y + v.y, z + v.z)

    operator fun plus(f: Double) = Point3D(x + f, y + f, z + f)

    operator fun minus(v: Point3D): Vector3D = Vector3D(x - v.x, y - v.y, z - v.z)

    operator fun minus(v: Vector3D) = Point3D(x - v.x, y - v.y, z - v.z)

    operator fun minus(f: Double) = Point3D(x - f, y - f, z - f)

    fun sqrDistance(p: Point3D): Double {
        val dx = x - p.x
        val dy = y - p.y
        val dz = z - p.z
        return dx * dx + dy * dy + dz * dz
    }

    fun ith(axis: Axis) =
        when (axis) {
            Axis.X -> x
            Axis.Y -> y
            Axis.Z -> z
        }

    override fun toString(): String = "($x,$y,$z)"

    companion object {
        val UNIT = Point3D(1.0, 1.0, 1.0)
        val ORIGIN = Point3D(0.0, 0.0, 0.0)
        val MAX = Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        val MIN = Point3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)
        val X = Point3D(1.0, 0.0, 0.0)
        val Y = Point3D(0.0, 1.0, 0.0)
        val Z = Point3D(0.0, 0.0, 1.0)
    }
}

infix operator fun Double.times(p: Point3D) = Point3D(this * p.x, this * p.y, this * p.z)
