package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.textures.Texture

class SVGlossySpecular : BRDF {

    /**
     * specular intensity
     */
    var ks: Double = 0.toDouble()

    // specular color
    var cs: Texture? = null

    // specular exponent
    var exp: Double = 0.toDouble()

    constructor() {
        this.ks = 0.25
        this.exp = 5.0
        this.cs = null
    }

    constructor(ks: Double, cs: Texture, exp: Double) {
        this.ks = ks
        this.cs = cs
        this.exp = exp
    }

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        assert(null != cs)
        val nDotWi = wi.dot(sr.normal)
        val r = wi.times(-1.0).plus(Vector3D(sr.normal).times(2 * nDotWi))
        val rDotWo = r.dot(wo)
        return if (rDotWo > 0) {
            cs!!.getColor(sr).mult(ks * Math.pow(rDotWo, exp))
        } else {
            Color.BLACK
        }
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        assert(null != cs)

        val sample = new()

        val nDotWo = wo.dot(sr.normal)

        val w = wo.times(-1.0).plus(Vector3D(sr.normal).times(2.0 * nDotWo))
        val u = Vector3D(0.00424, 1.0, 0.00764).cross(w).normalize()
        val v = u.cross(w)

        val sp = sampler!!.sampleSphere()
        sample.wi = u.times(sp.x).plus(v.times(sp.y)).plus(w.times(sp.z))
        if (sample.wi!!.dot(sr.normal) < 0.0) {
            sample.wi = u.times(-sp.x).plus(v.times(-sp.y)).plus(w.times(-sp.z))
        }

        val phongLobe = Math.pow(sample.wi!!.dot(w), exp)

        sample.pdf = phongLobe * sample.wi!!.dot(sr.normal)
        sample.color = cs!!.getColor(sr).mult(ks * phongLobe)
        return sample
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        throw RuntimeException("GlossySpecular.rho")
    }
}

