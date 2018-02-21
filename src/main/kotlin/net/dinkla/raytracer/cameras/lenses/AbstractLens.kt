package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D

abstract class AbstractLens(var viewPlane: ViewPlane?) : ILens {

    var eye: Point3D?

    //fun getEye(): Point3D? = this.eye

    var uvw: Basis?

    init {
        assert(null != viewPlane)
        this.eye = null
        this.uvw = null
    }

}
