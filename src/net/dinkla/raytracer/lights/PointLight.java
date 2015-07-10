package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3DF;
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
    public float ls;
    public C color;
    public Point3DF location;

    protected C cachedL;

    public PointLight(Point3DF location) {
        this.location = location;
        color = (C) C.getWhite();
        ls = 1.0f;
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
    public Vector3DF getDirection(Shade sr) {
        return new Vector3DF(location.minus(new Vector3DF(sr.getHitPoint()))).normalize();
    }

    @Override
    public boolean inShadow(World<C> world, Ray ray, Shade sr) {
        float d = location.minus(ray.o).length();
        return world.inShadow(ray, sr, d);
    }

}
