package net.dinkla.raytracer.tracers;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 14:56:41
 * To change this template use File | Settings | File Templates.
 */
public class RayCast extends Tracer {

    public RayCast(World world) {
        super(world);
        throw new RuntimeException("DO NOT USE");
    }

    @Override
    public Color trace(Ray ray, int depth) {
        Shade sr = new Shade();
        if (world.hit(ray, sr)) {
            sr.ray = ray;
            return (sr.getMaterial().shade(world, sr));
        } else {
            return world.getBackgroundColor();
        }
    }

}
