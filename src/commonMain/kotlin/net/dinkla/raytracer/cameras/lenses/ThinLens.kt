package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.UnitDiskSampler

/**
 * Thin-lens camera with depth of field, Suffern ch. 10.
 *
 * Unlike the [Pinhole] (all rays originate at a single point, so everything is in sharp focus), the
 * thin lens has a finite aperture of radius [lensRadius]. Each primary ray starts at a sample point
 * on the lens disk and is aimed at the point where the corresponding pinhole ray crosses the *focal
 * plane* at distance [f]. Consequently:
 *
 * - Scene points that lie on the focal plane (distance [f] from the eye) are sharp: every lens sample
 *   for a pixel converges on the same focal point.
 * - Points nearer or farther than [f] are hit by a *spread* of lens samples, so averaging many
 *   samples per pixel ([getRaySampled]) blurs them — the depth-of-field effect.
 *
 * Parameters:
 * - [d] — the view-plane distance (focal length); larger [d] narrows the field of view.
 * - [f] — the focal-plane distance; scene geometry at this distance renders sharp.
 * - [lensRadius] — the aperture radius; `0.0` collapses to a pinhole (no blur). Larger values widen
 *   the aperture and increase the blur of out-of-focus geometry.
 * - [sampler] — supplies unit-disk points that are scaled by [lensRadius] to place each ray's origin
 *   on the lens.
 *
 * Note on blur: visible blur only emerges when **many** lens samples are averaged per pixel via
 * [getRaySampled]. [getRaySingle] uses the lens centre and therefore produces the sharp,
 * pinhole-equivalent ray (the correct one-sample, no-blur fallback).
 */
class ThinLens(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    /** Supplies unit-disk points (scaled by [lensRadius]) that jitter the ray origin across the lens. */
    var sampler: UnitDiskSampler? = null

    /** Focal-plane distance; scene geometry at this distance from the eye renders sharp. */
    var f: Double = 1.0

    /** View-plane distance from the eye (focal length); larger values narrow the field of view. */
    var d: Double = 1.0

    /** Lens aperture radius; `0.0` is a pinhole (no blur), larger values blur out-of-focus geometry more. */
    var lensRadius: Double = 1.0

    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray {
        val px = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width)
        val py = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height)
        return rayThroughLens(px, py, 0.0, 0.0)
    }

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray {
        val px = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width + sp.x)
        val py = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height + sp.y)
        val theSampler = requireNotNull(sampler) { "ThinLens.sampler not set; assign a lens sampler before rendering" }
        val ls = theSampler.sampleUnitDisk()
        return rayThroughLens(px, py, lensRadius * ls.x, lensRadius * ls.y)
    }

    /**
     * Builds the depth-of-field ray for the view-plane point ([px], [py]) and the lens-disk sample
     * ([lx], [ly]) measured in world units along the camera's right/up axes. The origin is the lens
     * sample point `eye + lx*u + ly*v`; the direction aims at the focal point `(px*f/d, py*f/d)` on
     * the focal plane at distance [f], so all lens samples for an in-focus pixel converge there.
     */
    private fun rayThroughLens(
        px: Double,
        py: Double,
        lx: Double,
        ly: Double,
    ): Ray {
        val origin = eye + (uvw.u * lx) + (uvw.v * ly)
        val focalX = px * f / d
        val focalY = py * f / d
        val direction = uvw.pm(focalX - lx, focalY - ly, f).normalize()
        return Ray(origin, direction)
    }
}
