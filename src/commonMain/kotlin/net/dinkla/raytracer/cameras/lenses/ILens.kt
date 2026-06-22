package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Ray

/**
 * A camera lens model: maps a view-plane pixel to the primary ray cast through the scene.
 * Implementations differ in their projection — [Pinhole] (perspective), [ThinLens] (depth of field),
 * [FishEye] and [Spherical] — but all turn a pixel `(row, column)` into an eye ray.
 *
 * A `null` return means the pixel does not map to a valid ray (e.g. a [FishEye] pixel outside the
 * unit image circle) and should be skipped.
 */
interface ILens {
    /**
     * The ray through the centre of pixel ([r], [c]), used for single-sample (no anti-aliasing)
     * rendering. Returns `null` when the pixel maps to no valid ray.
     */
    fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray?

    /**
     * The ray through pixel ([r], [c]) offset by the in-pixel sample point [sp], used for
     * anti-aliased and lens sampling. Returns `null` when the sample maps to no valid ray.
     */
    fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray?
}
