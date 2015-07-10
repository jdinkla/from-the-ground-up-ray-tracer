package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.samplers.Sampler;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 14:16:43
 * To change this template use File | Settings | File Templates.
 */
public abstract class BRDF<C extends Color> {

    public Sampler sampler;

    public class Sample {
        public C color;
        public Vector3DF wi;
        public float pdf;
    }

    abstract public C f(final Shade sr, final Vector3DF wo, final Vector3DF wi);

    abstract public Sample sampleF(Shade sr, Vector3DF wo);

    abstract public C rho(Shade sr, Vector3DF wo);
    
}
