package net.dinkla.raytracer.tracers;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
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
public class AreaLighting<C extends Color> extends Tracer<C> {

    static final Logger LOGGER = Logger.getLogger(AreaLighting.class);

    public AreaLighting(World<C> world) {
        super(world);
    }

    @Override
    public C trace(Ray ray, int depth) {
        LOGGER.debug("trace " + ray + " at depth " + depth);
        if (depth > world.getViewPlane().maxDepth) {
            return (C) C.getBlack();
        } else {
            Shade sr = new Shade();
            if (world.hit(ray, sr)) {
                sr.depth = depth;
                sr.ray = ray;
                assert(null != sr.getMaterial());
                C result = (C) (sr.getMaterial().areaLightShade(world, sr));
                return result;
            } else {
                return world.getBackgroundColor();
            }
        }
    }
    
}