package net.dinkla.raytracer.tracers;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.WrappedFloat;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.04.2010
 * Time: 19:39:56
 * To change this template use File | Settings | File Templates.
 */
public class MultipleObjects<C extends Color> extends Tracer<C> {

    public MultipleObjects(World<C> world) {
        super(world);
    }

    @Override
    public C trace(Ray ray) {
        assert(null != ray);
        assert(null != ray.getO());
        assert(null != ray.getD());
        Shade sr = new Shade();
        if (world.hit(ray, sr)) {
            sr.ray = ray;
            return (C) sr.getMaterial().shade(world, sr);
        } else {
            return world.getBackgroundColor();
        }
    }

    @Override
    public C trace(Ray ray, int depth) {
        throw new RuntimeException("MultipleObjects.trace");
    }

    @Override
    public C trace(Ray ray, WrappedFloat tmin, int depth) {
        throw new RuntimeException("MultipleObjects.trace");
    }

}