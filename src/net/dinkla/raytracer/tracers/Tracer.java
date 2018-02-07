package net.dinkla.raytracer.tracers;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.WrappedFloat;
import net.dinkla.raytracer.worlds.World;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.04.2010
 * Time: 18:27:46
 * To change this template use File | Settings | File Templates.
 */
abstract public class Tracer<C extends Color> {

    /**
     * The logger.
     */
    static final Logger LOGGER = Logger.getLogger(Tracer.class);

    /**
     * The world.
     */
    public World<C> world;

    /**
     * Constructor.
     *
     * @param world
     */
    public Tracer(World<C> world) {
        this.world = world;
    }

    // TODO: Warum so viele trace-Funktionen?
    // TODO: Sollte nicht die mit den meisten Parametern abstract sein?
    abstract public C trace(Ray ray, final int depth);

    public C trace(Ray ray, WrappedFloat tmin, int depth) {
        //LOGGER.debug("trace " + ray + " tmin=" + tmin + " at depth " + depth);
        return trace(ray, depth);
    }

    public C trace(Ray ray) {
        //LOGGER.debug("trace " + ray);
        return trace(ray, 0);
    }    
}
