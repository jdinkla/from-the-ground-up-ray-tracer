package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.textures.Texture

/**
 * The spatially-varying counterpart of [Lambertian]: the diffuse colour `cd` is not a constant but
 * is sampled from a [Texture] at the hit point. Used by [net.dinkla.raytracer.materials.SvMatte] and
 * [net.dinkla.raytracer.materials.SvPhong].
 *
 * Mirrors Suffern's `SV_Lambertian` (Ray Tracing from the Ground Up, ch. 29).
 */
data class SvLambertian(
    var kd: Double = 1.0,
    var cd: Texture,
) : BRDF,
    ReflectanceBRDF {
    init {
        require(kd in 0.0..1.0) { "kd: diffuse reflection coefficient, in [0,1]" }
    }

    override fun f(
        sr: IShade,
        wo: Vector3D,
        wi: Vector3D,
    ): Color = cd.getColor(sr) * (kd * INV_PI)

    override fun rho(
        sr: IShade,
        wo: Vector3D,
    ): Color = cd.getColor(sr) * kd
}
