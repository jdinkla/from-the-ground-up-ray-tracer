package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D

interface BRDF {

    class Sample(
        val wi: Vector3D,
        val color: Color,
        val pdf: Double
    )

    fun f(sr: IShade, wo: Vector3D, wi: Vector3D): Color

    fun sampleF(sr: IShade, wo: Vector3D): Sample

    fun rho(sr: IShade, wo: Vector3D): Color
}
