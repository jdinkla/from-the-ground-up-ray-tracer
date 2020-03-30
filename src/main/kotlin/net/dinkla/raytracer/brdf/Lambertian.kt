package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.MathUtils.INV_PI
import java.util.Objects

// kd: diffuse reflection coefficient, in [0,1]
// cd: diffuse color

class Lambertian(var kd: Double = 1.0, var cd: Color = Color.WHITE) : BRDF() {

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color = cd.getColor(sr) * (kd * INV_PI)

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        throw RuntimeException("Lambertian.sampleF")
    }

    override fun rho(sr: Shade, wo: Vector3D): Color = cd.getColor(sr) * kd

    override fun equals(other: Any?): Boolean = if (other != null && other is Lambertian) {
        kd.equals(other.kd) && cd == other.cd
    } else {
        false
    }

    override fun hashCode(): Int = Objects.hash(kd, cd)

    override fun toString(): String = "Lambertian($kd,$cd)"
}

