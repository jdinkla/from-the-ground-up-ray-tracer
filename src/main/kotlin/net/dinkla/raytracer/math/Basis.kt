package net.dinkla.raytracer.math

import net.dinkla.raytracer.interfaces.hash
import net.dinkla.raytracer.utilities.equals

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

    override fun equals(other: Any?): Boolean =
            this.equals<Basis>(other) { a, b -> a.u == b.u && a.v == b.v && a.w == b.w }

    override fun hashCode(): Int = hash(u, v, w)

    override fun toString(): String = "Basis($u, $v, $w)"
}
