package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
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
    public Vector3D u, v, w;
    public Vector3D wi;

    @Override
    public boolean inShadow(World<C> world, Ray ray, Shade sr) {
        return world.inShadow(ray, sr, Float.MAX_VALUE);
    }

    @Override
    public Vector3D getDirection(Shade sr) {
        w = new Vector3D(sr.getNormal());
        v = new Vector3D(0.0034f, 1.0f, 0.0071f).cross(w);
        u = v.cross(w);
        Point3D sp = sampler.sampleHemisphere();
        wi = u.mult(sp.x).plus(v.mult(sp.y)).plus(w.mult(sp.z)); 
        return wi;
    }

    @Override
    public C L(World<C> world, Shade sr) {
        return (C) material.getLe(sr);
    }
}
