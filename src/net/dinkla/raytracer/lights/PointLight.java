package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 14:32:29
 * To change this template use File | Settings | File Templates.
 */
public class PointLight<C extends Color> extends Light<C> {

    // emissive material
    public double ls;
    public C color;
    public Point3D location;

    protected C cachedL;

    public PointLight(Point3D location) {
        this.location = location;
        color = (C) C.WHITE;
        ls = 1.0;
        shadows = true;
    }

    @Override
    public C L(World world, Shade sr) {
        assert null != color;
        if (null == cachedL) {
            cachedL = (C) color.mult(ls);
        }
        return cachedL;
    }

    @Override
    public Vector3D getDirection(Shade sr) {
        return new Vector3D(location.minus(new Vector3D(sr.getHitPoint()))).normalize();
    }

    @Override
    public boolean inShadow(World<C> world, Ray ray, Shade sr) {
        double d = location.minus(ray.getO()).length();
        return world.inShadow(ray, sr, d);
    }

}
