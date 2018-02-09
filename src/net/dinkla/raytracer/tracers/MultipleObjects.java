package net.dinkla.raytracer.tracers;

import net.dinkla.raytracer.colors.Color;
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
public class MultipleObjects extends Tracer {

    public MultipleObjects(World world) {
        super(world);
    }

    @Override
    public Color trace(Ray ray) {
        assert(null != ray);
        assert(null != ray.getO());
        assert(null != ray.getD());
        Shade sr = new Shade();
        if (world.hit(ray, sr)) {
            sr.ray = ray;
            return  sr.getMaterial().shade(world, sr);
        } else {
            return world.getBackgroundColor();
        }
    }

    @Override
    public Color trace(Ray ray, int depth) {
        throw new RuntimeException("MultipleObjects.trace");
    }

    @Override
    public Color trace(Ray ray, WrappedFloat tmin, int depth) {
        throw new RuntimeException("MultipleObjects.trace");
    }

}