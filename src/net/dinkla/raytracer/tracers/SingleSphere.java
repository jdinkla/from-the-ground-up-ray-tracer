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
public class SingleSphere extends Tracer {

    public SingleSphere(World world) {
        super(world);
    }

    @Override
    public Color trace(Ray ray) {
        if (world.hit(ray, new Shade())) {
            return Color.errorColor;
        } else {
            return world.getBackgroundColor();
        }

    }

    @Override
    public Color trace(Ray ray, int depth) {
        return null;
    }

    @Override
    public Color trace(Ray ray, WrappedFloat tmin, int depth) {
        return null; 
    }

}
