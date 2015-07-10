package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Vector3DF;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 15.05.2010
 * Time: 11:17:10
 * To change this template use File | Settings | File Templates.
 */
public class FresnelReflector<C extends Color> extends BRDF<C> {

    @Override
    public C f(final Shade sr, final Vector3DF wo, final Vector3DF wi){
        return null;
    }

    @Override
    public C rho(Shade sr, Vector3DF wo) {
        return null;
    }

    @Override
    public Sample sampleF(Shade sr, Vector3DF wo) {
        return null;
    }
}
