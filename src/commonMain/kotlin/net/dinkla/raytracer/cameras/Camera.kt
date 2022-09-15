package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

open class Camera(
    createLens: (Point3D, Basis) -> AbstractLens,
    val eye: Point3D = Point3D(0.0, 10.0, 0.0),
    val lookAt: Point3D = Point3D.ORIGIN,
    val up: Vector3D = Vector3D.UP
) {
    val uvw: Basis = Basis.create(eye, lookAt, up)
    val lens: AbstractLens = createLens(eye, uvw)
}
