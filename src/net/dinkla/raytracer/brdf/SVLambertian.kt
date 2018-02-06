package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.textures.Texture

import net.dinkla.raytracer.math.MathUtils.INV_PI

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 20:51:48
 * To change this template use File | Settings | File Templates.
 */
class SVLambertian<C : Color> : BRDF<C>() {

    var kd: Float = 0.toFloat()
    var cd: Texture<C>? = null

    init {
        kd = 1.0f
        cd = null
    }

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): C {
        return cd!!.getColor(sr).mult(kd * INV_PI) as C
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF<C>.Sample {
        throw RuntimeException("SVLambertian.sampleF")
    }

    override fun rho(sr: Shade, wo: Vector3D): C {
        return cd!!.getColor(sr).mult(kd) as C
    }


}
