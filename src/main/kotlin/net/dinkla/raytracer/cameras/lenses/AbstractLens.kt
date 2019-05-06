package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import java.util.zip.DeflaterOutputStream

abstract class AbstractLens(val viewPlane: ViewPlane) : ILens {
    var eye: Point3D? = null
    var uvw: Basis? = null

    companion object {
        internal val OFFSET: Double = 0.5
    }
}
