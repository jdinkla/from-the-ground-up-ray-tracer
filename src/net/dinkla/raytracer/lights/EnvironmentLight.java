package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.samplers.Sampler;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 27.05.2010
 * Time: 23:02:26
 * To change this template use File | Settings | File Templates.
 */
public class EnvironmentLight<C extends Color> extends Light<C> {

    public Sampler sampler;
    public Material<C> material;
    public Vector3DF u, v, w;
    public Vector3DF wi;

    @Override
    public boolean inShadow(World<C> world, Ray ray, Shade sr) {
        return world.inShadow(ray, sr, Float.MAX_VALUE);
    }

    @Override
    public Vector3DF getDirection(Shade sr) {
        w = new Vector3DF(sr.getNormal());
        v = new Vector3DF(0.0034f, 1.0f, 0.0071f).cross(w);
        u = v.cross(w);
        Point3DF sp = sampler.sampleHemisphere();
        wi = u.mult(sp.x).plus(v.mult(sp.y)).plus(w.mult(sp.z)); 
        return wi;
    }

    @Override
    public C L(World<C> world, Shade sr) {
        return (C) material.getLe(sr);
    }
}
