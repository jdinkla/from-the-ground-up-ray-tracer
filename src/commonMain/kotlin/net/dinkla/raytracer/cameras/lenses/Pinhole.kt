package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

/**
 * The pinhole (perspective) camera from Suffern ch. 8: all primary rays originate at the [eye] and
 * pass through the pixel's point on the view plane, giving a standard perspective projection with no
 * depth of field.
 *
 * [d] is the view-plane distance (focal length); larger [d] narrows the field of view (zooms in).
 */
class Pinhole(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    /** View-plane distance from the eye (focal length); larger values narrow the field of view. */
    var d: Double = 1.0

    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray {
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height)
        return Ray(eye, getRayDirection(x, y))
    }

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray {
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width + sp.x)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height + sp.y)
        return Ray(eye, getRayDirection(x, y))
    }

    /** The normalized world-space ray direction for a view-plane point ([x], [y]): `x*u + y*v - d*w`. */
    private fun getRayDirection(
        x: Double,
        y: Double,
    ): Vector3D = uvw.pm(x, y, d).normalize()
}
