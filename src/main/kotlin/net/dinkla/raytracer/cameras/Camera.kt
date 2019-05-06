package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

open class Camera(val lens: AbstractLens) {

    var eye: Point3D = Point3D(0, 10, 0)
    var lookAt: Point3D = Point3D.ORIGIN
    var up: Vector3D = Vector3D.UP
    var uvw: Basis = Basis(eye, lookAt, up)

    init {
        lens.eye = eye
        lens.uvw = uvw
    }

    fun setup(eye: Point3D, lookAt: Point3D, up: Vector3D) {
        this.eye = eye
        this.lookAt = lookAt
        this.up = up
        computeUVW()
    }

    fun computeUVW() {
        uvw = Basis(eye, lookAt, up)
        lens.eye = eye
        lens.uvw = uvw
    }
}
