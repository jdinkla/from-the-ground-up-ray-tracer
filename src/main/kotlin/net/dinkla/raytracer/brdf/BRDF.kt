package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

abstract class BRDF {

    class Sample(
            val wi: Vector3D,
            val color: Color,
            val pdf: Double
    )

    abstract fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color

    abstract fun sampleF(sr: Shade, wo: Vector3D): Sample

    abstract fun rho(sr: Shade, wo: Vector3D): Color
}
