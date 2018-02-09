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

    public void setKt(final double kt) {
        specularBtdf.setKt(kt);
    }

    public void setIor(final double ior) {
        specularBtdf.setIor(ior);
    }

    public void setKr(final double kr) {
        reflectiveBrdf.setKr(kr);
    }

    public void setCr(final Color cr) {
        reflectiveBrdf.setCr(cr);
    }

    @Override
    public Color shade(World world, Shade sr) {
        Color l = super.shade(world, sr);
        Vector3D wo = sr.ray.getD().mult(-1);
        BRDF.Sample brdf = reflectiveBrdf.sampleF(sr, wo);
        // trace reflected ray
        Ray reflectedRay = new Ray(sr.getHitPoint(), brdf.getWi());
        Color cr = world.getTracer().trace(reflectedRay, sr.depth + 1);
        if (specularBtdf.isTir(sr)) {
            l = l.plus(cr);
        } else {
            // reflected
            double cfr =  Math.abs(sr.getNormal().dot(brdf.getWi()));
            l = l.plus(brdf.getColor().mult(cr).mult(cfr));

            // trace transmitted ray
            BTDF.Sample btdf = specularBtdf.sampleF(sr, wo);
            Ray transmittedRay = new Ray(sr.getHitPoint(), btdf.getWt());
            Color ct = world.getTracer().trace(transmittedRay, sr.depth + 1);
            double cft =  Math.abs(sr.getNormal().dot(btdf.getWt()));
            l = l.plus(btdf.getColor().mult(ct).mult(cft));
        }
        return l;
    }
}
