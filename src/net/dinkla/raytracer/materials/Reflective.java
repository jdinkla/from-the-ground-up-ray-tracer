package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.brdf.BRDF;
import net.dinkla.raytracer.brdf.PerfectSpecular;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 24.04.2010
 * Time: 11:41:12
 * To change this template use File | Settings | File Templates.
 */
public class Reflective<C extends Color> extends Phong<C> {

    PerfectSpecular<C> reflectiveBrdf;

    public Reflective() {
        super();
        reflectiveBrdf = new PerfectSpecular<C>();
    }

    public void setKr(float kr) {
        reflectiveBrdf.kr = kr;
    }

    public void setCr(C cr) {
        reflectiveBrdf.cr = cr;
    }

    @Override
    public C shade(World<C> world, Shade sr) {
        final C L = super.shade(world, sr);
        final Vector3DF wo = sr.ray.d.negate();
        final BRDF.Sample sample = reflectiveBrdf.sampleF(sr, wo);
        float f = sr.getNormal().dot(sample.wi);
        final Ray reflectedRay = new Ray(sr.getHitPoint(), sample.wi);
        final C c1 = (C) world.getTracer().trace(reflectedRay, sr.depth + 1);
        final C c2 = (C) sample.color.mult(c1).mult(f);
        return (C) L.plus(c2);
    }
    
}
