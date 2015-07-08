package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.samplers.Sampler;

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
    public C f(final Shade sr, final Vector3D wo, final Vector3D wi) {
        assert null != cs;
        float nDotWi = wi.dot(sr.getNormal());
        Vector3D r = wi.mult(-1).plus(new Vector3D(sr.getNormal()).mult(2*nDotWi));
        float rDotWo = r.dot(wo);
        if (rDotWo > 0) {
            return (C) cs.mult((float) (ks * Math.pow(rDotWo, exp)));
        } else {
            return (C) C.getBlack();
        }
    }

    @Override
    public Sample sampleF(Shade sr, Vector3D wo) {
        assert null != cs;

        final Sample sample = new Sample();
        final float nDotWo = sr.getNormal().dot(wo);
        final Vector3D r = wo.negate().plus(sr.getNormal().mult(2 * nDotWo));

        final Vector3D w = r;
        final Vector3D u = new Vector3D(0.00424f, 1, 0.00764f).cross(w).normalize();
        final Vector3D v = u.cross(w);

        final Point3D sp = sampler.sampleHemisphere();
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
    public C rho(Shade sr, Vector3D wo) {
        throw new RuntimeException("GlossySpecular.rho");
    }

}