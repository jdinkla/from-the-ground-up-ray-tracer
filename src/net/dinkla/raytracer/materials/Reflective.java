package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.brdf.GlossySpecular;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.brdf.BRDF;
import net.dinkla.raytracer.brdf.PerfectSpecular;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

public class Reflective extends Phong {

    PerfectSpecular reflectiveBrdf;

    public Reflective() {
        super();
        reflectiveBrdf = new PerfectSpecular();
    }

    public void setKr(double kr) {
        reflectiveBrdf.setKr(kr);
    }

    public void setCr(Color cr) {
        reflectiveBrdf.setCr(cr);
    }

    @Override
    public Color shade(World world, Shade sr) {
        final Color L = super.shade(world, sr);
        final Vector3D wo = sr.ray.getD().negate();
        final BRDF.Sample sample = reflectiveBrdf.sampleF(sr, wo);
        double f = sr.getNormal().dot(sample.getWi());
        final Ray reflectedRay = new Ray(sr.getHitPoint(), sample.getWi());
        final Color c1 =  world.getTracer().trace(reflectedRay, sr.depth + 1);
        final Color c2 =  sample.getColor().mult(c1).mult(f);
        return  L.plus(c2);
    }
    
}
