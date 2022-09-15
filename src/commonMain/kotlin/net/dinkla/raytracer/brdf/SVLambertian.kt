package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.textures.Texture

class SVLambertian : BRDF {

    var kd: Double = 1.0
    var cd: Texture? = null

    override fun f(sr: IShade, wo: Vector3D, wi: Vector3D): Color {
        return cd!!.getColor(sr)* (kd * INV_PI)
    }

    override fun sampleF(sr: IShade, wo: Vector3D): Sample {
        throw RuntimeException("SVLambertian.sampleF")
    }

    override fun rho(sr: IShade, wo: Vector3D): Color {
        return cd!!.getColor(sr) * kd
    }

}
