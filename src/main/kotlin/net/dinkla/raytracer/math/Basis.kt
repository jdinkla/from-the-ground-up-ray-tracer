package net.dinkla.raytracer.math

class Basis {

    val u: Vector3D
    val v: Vector3D
    val w: Vector3D

    constructor(u: Vector3D, v: Vector3D, w: Vector3D) {
        this.u = u
        this.v = v
        this.w = w
    }

    constructor(eye: Point3D, lookAt: Point3D, up: Vector3D) {
        w = (eye - lookAt).normalize()
        u = (up cross w).normalize()
        v = w cross u
    }

    operator fun times(p: Point3D): Vector3D = (u * p.x) + (v * p.y) + (w * p.z)

    fun pm(x: Double, y: Double, z: Double): Vector3D = (u * x) + (v * y) - (w * z)

    fun pp(x: Double, y: Double, z: Double): Vector3D = (u * x) + (v * y) + (w * z)

}
