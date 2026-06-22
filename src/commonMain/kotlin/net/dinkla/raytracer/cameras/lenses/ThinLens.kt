package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler

/**
 * Thin-lens camera with depth of field in Suffern ch. 10, where [f] is the focal-plane distance, [d]
 * the view-plane distance, and [sampler] jitters the ray origin across the lens disk.
 *
 * NOTE: this implementation is a stub — it is **not** the working depth-of-field lens. Both
 * `getRaySingle` and `getRaySampled` ignore the pixel coordinates, the sample point, [f], [d] and
 * [sampler], and always return the single fixed ray from [eye] along `(u + v - w)` normalized. The
 * fields exist for the intended model but are currently unused.
 */
class ThinLens(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    /** Lens sampler intended to jitter the ray origin across the lens disk (currently unused). */
    var sampler: Sampler? = null

    /** Focal-plane distance (currently unused). */
    var f: Double = 1.0

    /** View-plane distance (currently unused). */
    var d: Double = 1.0

    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray = getRay()

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray = getRay()

    private fun getRay(): Ray = Ray(eye, getRayDirection())

    /** The fixed direction `(u + v - w)` normalized — independent of pixel, sample or focus (see class note). */
    private fun getRayDirection(): Vector3D = uvw.pm(1.0, 1.0, 1.0).normalize()
}
