package net.dinkla.raytracer.math

data class Basis(
    val u: Vector3D,
    val v: Vector3D,
    val w: Vector3D,
) {
    operator fun times(p: Point3D): Vector3D = (u * p.x) + (v * p.y) + (w * p.z)

    fun pm(
        x: Double,
        y: Double,
        z: Double,
    ): Vector3D = (u * x) + (v * y) - (w * z)

    fun pp(
        x: Double,
        y: Double,
        z: Double,
    ): Vector3D = (u * x) + (v * y) + (w * z)

    companion object {
        fun create(
            eye: Point3D,
            lookAt: Point3D,
            up: Vector3D,
        ): Basis {
            val w = (eye - lookAt).normalize()
            val u = (up cross w).normalize()
            val v = w cross u
            return Basis(u, v, w)
        }
    }
}
