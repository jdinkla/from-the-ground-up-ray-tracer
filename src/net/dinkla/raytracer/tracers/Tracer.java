package net.dinkla.raytracer.tracers;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.WrappedFloat;
import net.dinkla.raytracer.worlds.World;
import org.apache.log4j.Logger;

abstract public class Tracer {

    /**
     * The logger.
     */
    static final Logger LOGGER = Logger.getLogger(Tracer.class);

    /**
     * The world.
     */
    public World world;

    /**
     * Constructor.
     *
     * @param world
     */
    public Tracer(World world) {
        this.world = world;
    }

    // TODO: Warum so viele trace-Funktionen?
    // TODO: Sollte nicht die mit den meisten Parametern abstract sein?
    abstract public Color trace(Ray ray, final int depth);

    public Color trace(Ray ray, WrappedFloat tmin, int depth) {
        //LOGGER.debug("trace " + ray + " tmin=" + tmin + " at depth " + depth);
        return trace(ray, depth);
    }

    public Color trace(Ray ray) {
        //LOGGER.debug("trace " + ray);
        return trace(ray, 0);
    }    
}
