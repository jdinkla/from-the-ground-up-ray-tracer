package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Vector3D;

import static net.dinkla.raytracer.math.MathUtils.INV_PI;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 14:17:30
 * To change this template use File | Settings | File Templates.
 */
public class Lambertian<C extends Color> extends BRDF<C> {

    // diffuse reflection coefficient, in [0,1]
    public double kd;

    // diffuse color
    public C cd;

    public Lambertian() {
        kd = 1.0;
        cd = (C) C.WHITE;
    }

    @Override
    public C f(final Shade sr, final Vector3D wo, final Vector3D wi){
        return (C) cd.getColor(sr).mult(kd * INV_PI);
    }

    @Override
    public Sample sampleF(Shade sr, Vector3D wo) {
        throw new RuntimeException("Lambertian.sampleF");
    }

    @Override
    public C rho(Shade sr, Vector3D wo) {
        return (C) cd.getColor(sr).mult(kd);
    }

}

