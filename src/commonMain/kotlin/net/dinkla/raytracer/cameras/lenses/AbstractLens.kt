package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D

abstract class AbstractLens(val viewPlane: ViewPlane, val eye: Point3D, val uvw: Basis) : ILens {
    companion object {
        internal const val OFFSET: Double = 0.5
    }
}
