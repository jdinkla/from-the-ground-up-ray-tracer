package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D

abstract class AbstractLens(val viewPlane: ViewPlane) : ILens {
    var eye: Point3D? = null
    var uvw: Basis? = null
}
