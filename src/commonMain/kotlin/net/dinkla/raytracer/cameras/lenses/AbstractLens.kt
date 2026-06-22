package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D

/**
 * Common state for the concrete [ILens] implementations: the [viewPlane] being sampled, the camera
 * position [eye], and the orthonormal camera [Basis] [uvw] (right/up/back axes) that orients rays in
 * world space.
 */
abstract class AbstractLens(
    val viewPlane: ViewPlane,
    val eye: Point3D,
    val uvw: Basis,
) : ILens {
    companion object {
        /** Half-pixel offset that centres pixel coordinates on the view plane (pixel `i` maps to `i - 0.5*size`). */
        internal const val OFFSET: Double = 0.5
    }
}
