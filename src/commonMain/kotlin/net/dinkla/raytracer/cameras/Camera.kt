package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

open class Camera(private val lens: AbstractLens) {

    private var eye: Point3D = Point3D(0.0, 10.0, 0.0)
    private var lookAt: Point3D = Point3D.ORIGIN
    private var up: Vector3D = Vector3D.UP
    var uvw: Basis = Basis.create(eye, lookAt, up)
        private set

    init {
        computeUVW()
    }

    fun setup(eye: Point3D, lookAt: Point3D, up: Vector3D) {
        this.eye = eye
        this.lookAt = lookAt
        this.up = up
        computeUVW()
    }

    private fun computeUVW() {
        uvw = Basis.create(eye, lookAt, up)
        lens.eye = eye
        lens.uvw = uvw
    }
}
