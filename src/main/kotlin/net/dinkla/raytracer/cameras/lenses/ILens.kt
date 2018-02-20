package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Ray

interface ILens {

    fun getRaySingle(r: Int, c: Int): Ray?

    fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray?

//    fun getEye(): Point3D?

}
