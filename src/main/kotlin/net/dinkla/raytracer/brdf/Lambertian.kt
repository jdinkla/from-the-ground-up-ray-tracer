package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

import net.dinkla.raytracer.math.MathUtils.INV_PI
import java.util.*

class Lambertian : BRDF() {

    // diffuse reflection coefficient, in [0,1]
    var kd: Double = 1.0

    // diffuse color
    var cd: Color = Color.WHITE

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        return cd.getColor(sr).times(kd * INV_PI)
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        throw RuntimeException("Lambertian.sampleF")
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        return cd.getColor(sr).times(kd)
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Lambertian) {
            return kd.equals(other.kd) && cd.equals(other.cd)
        }
        return false
    }

    override fun hashCode(): Int = Objects.hash(kd, cd)

    override fun toString(): String = "Lambertian $kd $cd"
}

