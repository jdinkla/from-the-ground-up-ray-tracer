package net.dinkla.raytracer.lights

import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D

interface ILightSource {

    fun sample(): Point3D

    fun pdf(sr: Shade): Double

    fun getNormal(p: Point3D): Normal

}
