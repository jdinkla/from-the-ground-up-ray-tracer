package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

class FresnelReflector : BRDF() {

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        return Color.BLACK
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        return Color.BLACK
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        return new()
    }
}
