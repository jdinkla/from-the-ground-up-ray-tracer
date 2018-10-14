package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.textures.Texture

import net.dinkla.raytracer.math.MathUtils.INV_PI

class SVLambertian : BRDF() {

    var kd: Double = 1.0
    var cd: Texture? = null

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        return cd!!.getColor(sr)* (kd * INV_PI)
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        throw RuntimeException("SVLambertian.sampleF")
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        return cd!!.getColor(sr) * kd
    }

}
