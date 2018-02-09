package net.dinkla.raytracer.tracers;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.worlds.World;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 14:56:41
 * To change this template use File | Settings | File Templates.
 */
public class AreaLighting extends Tracer {

    static final Logger LOGGER = Logger.getLogger(AreaLighting.class);

    public AreaLighting(World world) {
        super(world);
    }

    @Override
    public Color trace(Ray ray, int depth) {
        LOGGER.debug("trace " + ray + " at depth " + depth);
        if (depth > world.getViewPlane().maxDepth) {
            return Color.BLACK;
        } else {
            Shade sr = new Shade();
            if (world.hit(ray, sr)) {
                sr.depth = depth;
                sr.ray = ray;
                assert(null != sr.getMaterial());
                Color result =  (sr.getMaterial().areaLightShade(world, sr));
                return result;
            } else {
                return world.getBackgroundColor();
            }
        }
    }
    
}