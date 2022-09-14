package net.dinkla.raytracer.lights

import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D

interface ILightSource {

    fun sample(): Point3D

    fun pdf(sr: IShade): Double

    fun getNormal(p: Point3D): Normal

    fun getLightMaterial() : IMaterial
}
