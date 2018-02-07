package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;
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
    public C f(final Shade sr, final Vector3D wo, final Vector3D wi) {
        assert null != cs;
        float nDotWi = wi.dot(sr.getNormal());
        Vector3D r = wi.mult(-1).plus(new Vector3D(sr.getNormal()).mult(2*nDotWi));
        float rDotWo = r.dot(wo);
        if (rDotWo > 0) {
            return (C) cs.getColor(sr).mult((float) (ks * Math.pow(rDotWo, exp)));
        } else {
            return (C) C.BLACK;
        }
    }

    @Override
    public Sample sampleF(Shade sr, Vector3D wo) {
        assert null != cs;

        Sample sample = new Sample();

        float nDotWo = wo.dot(sr.getNormal());

        Vector3D w = wo.mult(-1).plus(new Vector3D(sr.getNormal()).mult(2.0f * nDotWo));
        Vector3D u = new Vector3D(0.00424f, 1, 0.00764f).cross(w).normalize();
        Vector3D v = u.cross(w);

        Point3D sp = sampler.sampleSphere();
        sample.wi = u.mult(sp.getX()).plus(v.mult(sp.getY())).plus(w.mult(sp.getZ()));
        if (sample.wi.dot(sr.getNormal()) < 0.0f) {
            sample.wi = u.mult(-sp.getX()).plus(v.mult(-sp.getY())).plus(w.mult(-sp.getZ()));
        }

        float phongLobe = (float) Math.pow(sample.wi.dot(w), exp);

        sample.pdf = phongLobe * (sample.wi.dot(sr.getNormal()));
        sample.color = (C) cs.getColor(sr).mult(ks * phongLobe);
        return sample;
    }

    @Override
    public C rho(Shade sr, Vector3D wo) {
        throw new RuntimeException("GlossySpecular.rho");
    }
}

