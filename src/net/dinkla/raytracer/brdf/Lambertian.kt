package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

import net.dinkla.raytracer.math.MathUtils.INV_PI

class Lambertian : BRDF() {

    // diffuse reflection coefficient, in [0,1]
    var kd: Double = 0.toDouble()

    // diffuse color
    var cd: Color

    init {
        kd = 1.0
        cd = Color.WHITE
    }

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        return cd.getColor(sr).mult(kd * INV_PI)
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        throw RuntimeException("Lambertian.sampleF")
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        return cd.getColor(sr).mult(kd)
    }

}

