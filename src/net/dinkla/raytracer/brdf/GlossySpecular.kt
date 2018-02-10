package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler

class GlossySpecular : BRDF {

    /**
     * specular intensity
     */
    var ks: Double = 0.toDouble()

    // specular color
    var cs: Color? = null

    // specular exponent
    var exp: Double = 0.toDouble()

    constructor() {
        this.ks = 0.25
        this.exp = 5.0
        this.cs = Color.WHITE
    }

    constructor(ks: Double, cs: Color, exp: Double) {
        this.ks = ks
        this.cs = cs
        this.exp = exp
    }

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        assert(null != cs)
        val nDotWi = wi.dot(sr.normal)
        val r = wi.mult(-1.0).plus(Vector3D(sr.normal).mult(2 * nDotWi))
        val rDotWo = r.dot(wo)
        return if (rDotWo > 0) {
            cs!!.mult(ks * Math.pow(rDotWo, exp))
        } else {
            Color.BLACK
        }
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        assert(null != cs)

        val sample = new()
        val nDotWo = sr.normal?.dot(wo)
        val r = wo.negate().plus(sr.normal?.mult(2 * nDotWo))

        val u = Vector3D(0.00424, 1.0, 0.00764).cross(r).normalize()
        val v = u.cross(r)

        val sp = sampler!!.sampleHemisphere()
        sample.wi = u.mult(sp.x).plus(v.mult(sp.y)).plus(r.mult(sp.z))
        val nDotWi = sr.normal.dot(sample.wi!!)
        if (nDotWi < 0) {
            sample.wi = u.mult(-sp.x).plus(v.mult(-sp.y)).plus(r.mult(-sp.z))
        }

        val phongLobe = Math.pow(sample.wi!!.dot(r), exp)

        sample.pdf = phongLobe * nDotWi
        sample.color = cs!!.mult(ks * phongLobe)
        return sample
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        throw RuntimeException("GlossySpecular.rho")
    }

}