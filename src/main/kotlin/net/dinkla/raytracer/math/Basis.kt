package net.dinkla.raytracer.math

class Basis(val eye: Point3D, val lookAt: Point3D, val up: Vector3D) {

    val u: Vector3D
    val v: Vector3D
    val w: Vector3D

    init {
        w = (eye - lookAt).normalize()
        u = (up cross w).normalize()
        v = w cross u
    }

    fun pm(x: Double, y: Double, z: Double): Vector3D = (u * x) + (v * y) - (w * z)

    fun pp(x: Double, y: Double, z: Double): Vector3D = (u * x) + (v * y) + (w * z)

}
