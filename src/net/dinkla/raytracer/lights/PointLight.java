package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;


public class PointLight extends Light {

    // emissive material
    public double ls;
    public Color color;
    public Point3D location;

    protected Color cachedL;

    public PointLight(Point3D location) {
        this.location = location;
        color = Color.WHITE;
        ls = 1.0;
        setShadows(true);
    }

    @Override
    public Color L(World world, Shade sr) {
        assert null != color;
        if (null == cachedL) {
            cachedL = color.mult(ls);
        }
        return cachedL;
    }

    @Override
    public Vector3D getDirection(Shade sr) {
        return new Vector3D(location.minus(new Vector3D(sr.getHitPoint()))).normalize();
    }

    @Override
    public boolean inShadow(World world, Ray ray, Shade sr) {
        double d = location.minus(ray.getO()).length();
        return world.inShadow(ray, sr, d);
    }

}
