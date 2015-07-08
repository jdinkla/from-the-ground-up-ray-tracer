package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Vector3D;
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
        public Vector3D wi;
        public float pdf;
    }

    abstract public C f(final Shade sr, final Vector3D wo, final Vector3D wi);

    abstract public Sample sampleF(Shade sr, Vector3D wo);

    abstract public C rho(Shade sr, Vector3D wo);
    
}
