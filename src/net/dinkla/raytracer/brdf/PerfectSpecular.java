package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Normal;
import net.dinkla.raytracer.math.Vector3D;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 24.04.2010
 * Time: 11:25:05
 * To change this template use File | Settings | File Templates.
 */
public class PerfectSpecular<C extends Color> extends BRDF<C> {

    public double kr;
    public C cr;

    public PerfectSpecular() {
        super();
        kr = 1.0;
        cr = (C) C.WHITE;
    }

    @Override
    public C f(final Shade sr, final Vector3D wo, final Vector3D wi){
        throw new RuntimeException("PerfectSpecular.f");
        // Im C-Code Black
    }

    @Override
    public Sample sampleF(Shade sr, Vector3D wo) {
        assert null != cr;
        final Sample result = new Sample();
        Normal normal = sr.getNormal();
        final double nDotWo = normal.dot(wo);
        result.wi = wo.negate().plus(sr.getNormal().mult(2.0 * nDotWo));
        final double nDotWi = normal.dot(result.wi);
        result.color = (C) cr.mult(kr / Math.abs(nDotWi));
        return result;
    }

    @Override
    public C rho(Shade sr, Vector3D wo) {
        throw new RuntimeException("PerfectSpecular.rho");
    }

}
