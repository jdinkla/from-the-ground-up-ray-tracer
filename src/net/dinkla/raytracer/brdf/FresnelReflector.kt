package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Vector3D

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 15.05.2010
 * Time: 11:17:10
 * To change this template use File | Settings | File Templates.
 */
class FresnelReflector<C : Color> : BRDF<C>() {

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): C? {
        return null
    }

    override fun rho(sr: Shade, wo: Vector3D): C? {
        return null
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF<C>.Sample? {
        return null
    }
}
