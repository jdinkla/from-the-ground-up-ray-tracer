package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

open class Camera(
    createLens: (Point3D, Basis) -> AbstractLens,
    val eye: Point3D = Point3D(0.0, 10.0, 0.0),
    val lookAt: Point3D = Point3D.ORIGIN,
    val up: Vector3D = Vector3D.UP,
) {
    val uvw: Basis = Basis.create(eye, lookAt, up)
    val lens: AbstractLens = createLens(eye, uvw)

    /**
     * Multiplier applied to the radiance carried by each primary ray (Suffern's `exposure_time`,
     * default `1.0`). It is honoured by the single-ray renderers, so it scales the colour of every
     * camera/lens type uniformly. The book reduces it (to `1/eta²`) when the camera sits *inside* a
     * dense transparent medium: crossing the surface scales radiance by `(eta_in/eta_out)²`
     * (about `5.86` for diamond), washing the interior view out unless exposure is lowered to
     * compensate (book §28.6.3, Figs 28.34/28.50). At the default `1.0` the colour is unchanged.
     */
    var exposureTime: Double = 1.0
}
