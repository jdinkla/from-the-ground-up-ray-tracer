package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

class FresnelReflector : BRDF() {
    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color = Color.BLACK

    override fun rho(sr: Shade, wo: Vector3D): Color = Color.BLACK

    // TODO def
    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample = Sample(
            wi = Vector3D.ZERO, color = Color.BLACK, pdf = 1.0)
}
