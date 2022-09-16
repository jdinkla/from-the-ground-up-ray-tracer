package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Vector3D

// kd: diffuse reflection coefficient, in [0,1]
// cd: diffuse color
data class Lambertian(var kd: Double = 1.0, var cd: Color = Color.WHITE) : BRDF {

    override fun f(sr: IShade, wo: Vector3D, wi: Vector3D): Color = cd * (kd * INV_PI)

    override fun sampleF(sr: IShade, wo: Vector3D): Sample {
        throw RuntimeException("Lambertian.sampleF")
    }

    override fun rho(sr: IShade, wo: Vector3D): Color = cd * kd
}

