package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import java.util.Objects
import kotlin.math.abs
import kotlin.math.sqrt

class PerfectTransmitter(var ior: Double = 1.0, var kt: Double = 1.0) : BTDF() {

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        throw RuntimeException("PerfectTransmitter.f")
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        throw RuntimeException("PerfectTransmitter.rho")
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BTDF.Sample {
        var n = sr.normal
        var cosThetaI = n dot wo
        var eta = ior
        if (cosThetaI < 0) {
            cosThetaI = -cosThetaI
            n = -n
            eta = 1.0 / eta
        }
        val cosThetaTSqr = 1.0 - (1.0 - cosThetaI * cosThetaI) / (eta * eta)
        val cosThetaT = sqrt(cosThetaTSqr)
        val wt = (wo * -eta) - n * (cosThetaT - cosThetaI / eta)
        val f1 = kt / (eta * eta)
        val f2 = sr.normal dot wt
        return Sample(wt = wt, color = Color.WHITE.times(f1 / abs(f2)))
    }

    override fun isTir(sr: Shade): Boolean {
        val wo = sr.ray.direction * -1.0
        val cosThetaI = wo dot sr.normal
        var eta = ior
        if (cosThetaI < 0) {
            eta = 1.0 / eta
        }
        val cosThetaTSqr = 1.0 - (1.0 - cosThetaI * cosThetaI) / (eta * eta)
        return cosThetaTSqr < 0
    }

    override fun equals(other: Any?): Boolean = if (other != null && other is PerfectTransmitter) {
        this.ior == other.ior && this.kt == other.kt
    } else {
        false
    }

    override fun hashCode(): Int = Objects.hash(ior, kt)

    override fun toString() = "PerfectTransmitter($ior, $kt)"
}
