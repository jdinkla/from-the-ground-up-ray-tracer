package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.textures.Texture;

import static net.dinkla.raytracer.math.MathUtils.INV_PI;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 20:51:48
 * To change this template use File | Settings | File Templates.
 */
public class SVLambertian<C extends Color> extends BRDF<C> {

    public float kd;
    public Texture<C> cd;

    public SVLambertian() {
        kd = 1.0f;
        cd = null;
    }

    @Override
    public C f(Shade sr, Vector3DF wo, Vector3DF wi) {
        return  (C) cd.getColor(sr).mult(kd * INV_PI);
    }

    @Override
    public Sample sampleF(Shade sr, Vector3DF wo) {
        throw new RuntimeException("SVLambertian.sampleF");
    }

    @Override
    public C rho(Shade sr, Vector3DF wo) {
        return (C) cd.getColor(sr).mult(kd);
    }

    

}
