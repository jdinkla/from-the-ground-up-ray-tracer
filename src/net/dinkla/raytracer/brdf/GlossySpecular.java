package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Vector3DF;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 14:17:30
 * To change this template use File | Settings | File Templates.
 */
public class GlossySpecular<C extends Color> extends BRDF<C> {

    /**
     * specular intensity
     */
    public float ks;

    // specular color
    public C cs;

    // specular exponent
    public float exp;

    public GlossySpecular() {
        this.ks = 0.25f;
        this.exp = 5.0f;
        this.cs = (C) C.getWhite();
    }

    public GlossySpecular(float ks, C cs, float exp) {
        this.ks = ks;
        this.cs = cs;
        this.exp = exp;
    }

    @Override
    public C f(final Shade sr, final Vector3DF wo, final Vector3DF wi) {
        assert null != cs;
        float nDotWi = wi.dot(sr.getNormal());
        Vector3DF r = wi.mult(-1).plus(new Vector3DF(sr.getNormal()).mult(2*nDotWi));
        float rDotWo = r.dot(wo);
        if (rDotWo > 0) {
            return (C) cs.mult((float) (ks * Math.pow(rDotWo, exp)));
        } else {
            return (C) C.getBlack();
        }
    }

    @Override
    public Sample sampleF(Shade sr, Vector3DF wo) {
        assert null != cs;

        final Sample sample = new Sample();
        final float nDotWo = sr.getNormal().dot(wo);
        final Vector3DF r = wo.negate().plus(sr.getNormal().mult(2 * nDotWo));

        final Vector3DF w = r;
        final Vector3DF u = new Vector3DF(0.00424f, 1, 0.00764f).cross(w).normalize();
        final Vector3DF v = u.cross(w);

        final Point3DF sp = sampler.sampleHemisphere();
        sample.wi = u.mult(sp.x).plus(v.mult(sp.y)).plus(w.mult(sp.z));
        final float nDotWi = sr.getNormal().dot(sample.wi);
        if (nDotWi < 0) {
            sample.wi = u.mult(-sp.x).plus(v.mult(-sp.y)).plus(w.mult(-sp.z));
        }

        final float phongLobe = (float) Math.pow(sample.wi.dot(r), exp);

        sample.pdf = phongLobe * nDotWi;
        sample.color = (C) cs.mult(ks * phongLobe);
        return sample;
    }

    @Override
    public C rho(Shade sr, Vector3DF wo) {
        throw new RuntimeException("GlossySpecular.rho");
    }

}