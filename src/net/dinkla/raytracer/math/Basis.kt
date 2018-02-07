package net.dinkla.raytracer.math

class Basis(eye: Point3D, lookAt: Point3D, up: Vector3D) {

    val u: Vector3D
    val v: Vector3D
    val w: Vector3D

    init {
        w = eye.minus(lookAt).normalize()
        u = up.cross(w).normalize()
        v = w.cross(u)
    }

    fun pm(x: Double, y: Double, z: Double): Vector3D {
        return u.mult(x).plus(v.mult(y)).minus(w.mult(z))
    }

    fun pp(x: Double, y: Double, z: Double): Vector3D {
        return u.mult(x).plus(v.mult(y)).plus(w.mult(z))
    }

}
