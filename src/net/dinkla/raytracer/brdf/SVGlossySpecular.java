package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.textures.Texture;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 03.06.2010
 * Time: 18:48:16
 * To change this template use File | Settings | File Templates.
 */
public class SVGlossySpecular<C extends Color> extends BRDF<C> {

    /**
     * specular intensity
     */
    public float ks;

    // specular color
    public Texture<C> cs;

    // specular exponent
    public float exp;

    public SVGlossySpecular() {
        this.ks = 0.25f;
        this.exp = 5.0f;
        this.cs = null;
    }

    public SVGlossySpecular(float ks, Texture<C> cs, float exp) {
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
            return (C) cs.getColor(sr).mult((float) (ks * Math.pow(rDotWo, exp)));
        } else {
            return (C) C.getBlack();
        }
    }

    @Override
    public Sample sampleF(Shade sr, Vector3DF wo) {
        assert null != cs;

        Sample sample = new Sample();

        float nDotWo = wo.dot(sr.getNormal());

        Vector3DF w = wo.mult(-1).plus(new Vector3DF(sr.getNormal()).mult(2.0f * nDotWo));
        Vector3DF u = new Vector3DF(0.00424f, 1, 0.00764f).cross(w).normalize();
        Vector3DF v = u.cross(w);

        Point3DF sp = sampler.sampleSphere();
        sample.wi = u.mult(sp.x()).plus(v.mult(sp.y())).plus(w.mult(sp.z()));
        if (sample.wi.dot(sr.getNormal()) < 0.0f) {
            sample.wi = u.mult(-sp.x()).plus(v.mult(-sp.y())).plus(w.mult(-sp.z()));
        }

        float phongLobe = (float) Math.pow(sample.wi.dot(w), exp);

        sample.pdf = phongLobe * (sample.wi.dot(sr.getNormal()));
        sample.color = (C) cs.getColor(sr).mult(ks * phongLobe);
        return sample;
    }

    @Override
    public C rho(Shade sr, Vector3DF wo) {
        throw new RuntimeException("GlossySpecular.rho");
    }
}

