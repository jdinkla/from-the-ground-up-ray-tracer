package net.dinkla.raytracer.btdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.samplers.Sampler;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 01.05.2010
 * Time: 09:53:32
 * To change this template use File | Settings | File Templates.
 */
public abstract class BTDF<C extends Color> {

    Sampler sampler;

    public class Sample {
        public C color;
        public Vector3DF wt;
    }

    abstract public C f(Shade sr, Vector3DF wo, Vector3DF wi);

    abstract public Sample sampleF(Shade sr, Vector3DF wo);

    abstract public C rho(Shade sr, Vector3DF wo);

    abstract public boolean isTir(Shade sr);
}
