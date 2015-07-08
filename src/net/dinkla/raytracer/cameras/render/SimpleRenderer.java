package net.dinkla.raytracer.cameras.render;

import net.dinkla.raytracer.cameras.lenses.ILens;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.tracers.Tracer;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 08.06.2010
 * Time: 19:33:28
 * To change this template use File | Settings | File Templates.
 */
public class SimpleRenderer implements ISingleRayRenderer {

    protected ILens lens;
    protected Tracer tracer;

    public SimpleRenderer(final ILens lens, final Tracer tracer) {
        this.lens = lens;
        this.tracer = tracer;
    }

    public Color render(int r, int c) {
        Ray ray = lens.getRaySingle(r, c);
        return tracer.trace(ray);
    }

}
