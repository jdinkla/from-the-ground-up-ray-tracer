package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.Point2D;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.samplers.Sampler;
import net.dinkla.raytracer.worlds.World;

public class EnvironmentLight extends Light {

    public Sampler sampler;
    public Material material;
    public Vector3D u, v, w;
    public Vector3D wi;

    @Override
    public boolean inShadow(World world, Ray ray, Shade sr) {
        return world.inShadow(ray, sr, Double.MAX_VALUE);
    }

    @Override
    public Vector3D getDirection(Shade sr) {
        w = new Vector3D(sr.getNormal());
        v = new Vector3D(0.0034, 1.0, 0.0071).cross(w);
        u = v.cross(w);
        Point3D sp = sampler.sampleHemisphere();
        wi = u.mult(sp.getX()).plus(v.mult(sp.getY())).plus(w.mult(sp.getZ()));
        return wi;
    }

    @Override
    public Color L(World world, Shade sr) {
        return  material.getLe(sr);
    }
}
