package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

class PerfectTransmitter : BTDF() {

    var ior: Double = 0.toDouble()
    var kt: Double = 0.toDouble()

    init {
        kt = 1.0
        ior = 1.0
    }

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        throw RuntimeException("PerfectTransmitter.f")
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        throw RuntimeException("PerfectTransmitter.rho")
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BTDF.Sample {
        val result = newSample()
        var n = sr.normal
        var cosThetaI = n.dot(wo)
        var eta = ior
        if (cosThetaI < 0) {
            cosThetaI = -cosThetaI
            n = n.negate()
            eta = 1.0 / eta
        }
        val cosThetaTSqr = 1.0 - (1.0 - cosThetaI * cosThetaI) / (eta * eta)
        val cosThetaT = Math.sqrt(cosThetaTSqr)
        result.wt = wo.times(-eta).minus(n.mult(cosThetaT - cosThetaI / eta))
        val f1 = kt / (eta * eta)
        val f2 = sr.normal.dot(result.wt!!)
        result.color = Color.WHITE.times(f1 / Math.abs(f2))
        return result
    }

    override fun isTir(sr: Shade): Boolean {
        val wo = sr.ray.d.times(-1.0)
        val cosThetaI = wo.dot(sr.normal)
        var eta = ior
        if (cosThetaI < 0) {
            eta = 1.0 / eta
        }
        val cosThetaTSqr = 1.0 - (1.0 - cosThetaI * cosThetaI) / (eta * eta)
        return cosThetaTSqr < 0
    }

}
