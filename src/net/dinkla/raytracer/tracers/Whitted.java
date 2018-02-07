package net.dinkla.raytracer.tracers;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.MathUtils;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.WrappedFloat;
import net.dinkla.raytracer.utilities.Counter;
import net.dinkla.raytracer.worlds.World;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 24.04.2010
 * Time: 10:59:02
 * To change this template use File | Settings | File Templates.
 */
public class Whitted<C extends Color> extends Tracer<C> {

    static final Logger LOGGER = Logger.getLogger(Whitted.class);

//    float f = 0.6f;
//    Color fc = new RGBColor(0.9f, 0.1f, 1.0);
//    Color fc = new RGBColor(0.5, 0.5, 0.5);

    public Whitted(World<C> world) {
        super(world);
    }

    @Override
    public C trace(Ray ray) {
        Counter.count("Whitted.trace1");
        return trace(ray, 0);
    }

    @Override
    public C trace(Ray ray, int depth) {
        Counter.count("Whitted.trace2");
        return trace(ray, WrappedFloat.createMax(), depth);
    }

    @Override
    public C trace(Ray ray, WrappedFloat tmin, int depth) {
        //LOGGER.debug("trace " + ray + " at depth " + depth);
        Counter.count("Whitted.trace3");
        C color = null;
        if (depth > world.getViewPlane().maxDepth) {
            color = (C) C.BLACK;
        } else {
            Shade sr = new Shade();
            boolean hit = world.hit(ray, sr);
            if (hit) {
                sr.depth = depth;
                sr.ray = ray;
                tmin.setValue(sr.t);
                if (null == sr.getMaterial()) {
                    LOGGER.error("Material is NULL for ray " + ray + " and sr " + sr);
                    color = (C) C.errorColor;
                } else {
                    color = (C) sr.getMaterial().shade(world, sr);
                }
            } else {
                // No hit -> Background
                tmin.setValue(MathUtils.K_HUGEVALUE);
                color = (C) world.getBackgroundColor();
            }
        }
/*
        float ff = (float) Math.sqrt(tmin.getValue() * f);
        color = (C) color.plus(fc.mult(ff));
*/
        return color;
    }

}
