package net.dinkla.raytracer.math

class Basis(eye: Point3D, lookAt: Point3D, up: Vector3D) {

    var u: Vector3D
    var v: Vector3D
    var w: Vector3D

    init {
        w = eye.minus(lookAt).normalize()
        u = up.cross(w).normalize()
        v = w.cross(u)
    }

    fun pm(x: Float, y: Float, z: Float): Vector3D {
        return u.mult(x).plus(v.mult(y)).minus(w.mult(z))
    }

    fun pp(x: Float, y: Float, z: Float): Vector3D {
        return u.mult(x).plus(v.mult(y)).plus(w.mult(z))
    }

}
