package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Vector3D

data class Lambertian(var kd: Double = 1.0, var cd: Color = Color.WHITE) : BRDF {

    init {
        if (kd < 0.0 || kd > 1.0) {
            throw AssertionError("kd: diffuse reflection coefficient, in [0,1]")
        }
    }

    override fun f(sr: IShade, wo: Vector3D, wi: Vector3D): Color = f

    override fun sampleF(sr: IShade, wo: Vector3D): Sample {
        throw RuntimeException("Lambertian.sampleF")
    }

    override fun rho(sr: IShade, wo: Vector3D): Color = rho

    val f: Color
        get() = cd * (kd * INV_PI)

    val rho: Color
        get() = cd * kd

}

