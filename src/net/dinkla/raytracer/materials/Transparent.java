package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.brdf.BRDF;
import net.dinkla.raytracer.brdf.PerfectSpecular;
import net.dinkla.raytracer.btdf.BTDF;
import net.dinkla.raytracer.btdf.PerfectTransmitter;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 01.05.2010
 * Time: 10:10:41
 * To change this template use File | Settings | File Templates.
 */
public class Transparent extends Phong {

    public PerfectSpecular reflectiveBrdf;
    public PerfectTransmitter specularBtdf;

    public Transparent() {
        this.reflectiveBrdf = new PerfectSpecular();
        this.specularBtdf = new PerfectTransmitter();
    }

    public void setKt(final float kt) {
        specularBtdf.kt = kt;
    }

    public void setIor(final float ior) {
        specularBtdf.ior = ior;
    }

    public void setKr(final float kr) {
        reflectiveBrdf.kr = kr;
    }

    public void setCr(final Color cr) {
        reflectiveBrdf.cr = cr;
    }

    @Override
    public Color shade(World world, Shade sr) {
        Color l = super.shade(world, sr);
        Vector3D wo = sr.ray.getD().mult(-1);
        BRDF.Sample brdf = reflectiveBrdf.sampleF(sr, wo);
        // trace reflected ray
        Ray reflectedRay = new Ray(sr.getHitPoint(), brdf.wi);
        Color cr = world.getTracer().trace(reflectedRay, sr.depth + 1);
        if (specularBtdf.isTir(sr)) {
            l = l.plus(cr);
        } else {
            // reflected
            float cfr = (float) Math.abs(sr.getNormal().dot(brdf.wi));
            l = l.plus(brdf.color.mult(cr).mult(cfr));

            // trace transmitted ray
            BTDF.Sample btdf = specularBtdf.sampleF(sr, wo);
            Ray transmittedRay = new Ray(sr.getHitPoint(), btdf.wt);
            Color ct = world.getTracer().trace(transmittedRay, sr.depth + 1);
            float cft = (float) Math.abs(sr.getNormal().dot(btdf.wt));
            l = l.plus(btdf.color.mult(ct).mult(cft));
        }
        return l;
    }
}
