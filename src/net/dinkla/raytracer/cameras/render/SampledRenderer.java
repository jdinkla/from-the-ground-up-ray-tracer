package net.dinkla.raytracer.cameras.render;

import net.dinkla.raytracer.cameras.lenses.ILens;
import net.dinkla.raytracer.colors.ColorAccumulator;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.math.Point2D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.samplers.MultiJittered;
import net.dinkla.raytracer.samplers.Sampler;
import net.dinkla.raytracer.tracers.Tracer;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 08.06.2010
 * Time: 19:34:35
 * To change this template use File | Settings | File Templates.
 */
public class SampledRenderer<C extends Color> implements ISingleRayRenderer {

    public ILens lens;
    public Tracer tracer;
    // Used for anti-aliasing
    public Sampler sampler;
    public int numSamples;

    public SampledRenderer(final ILens lens, final Tracer tracer) {
        this.lens = lens;
        this.tracer = tracer;
        this.numSamples = 1;
        this.sampler = new Sampler(new MultiJittered(), 2500, 10);
    }

    public Color render(int r, int c) {
        ColorAccumulator<C> color = new ColorAccumulator<C>();
        for (int j = 0; j < numSamples; j++) {
            Point2D sp = sampler.sampleUnitSquare();
            Ray ray = lens.getRaySampled(r, c, sp);
            color.plus(tracer.trace(ray));
        }
        return color.getAverage();
    }
    
}
