package net.dinkla.raytracer.brdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.math.Vector3D;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 15.05.2010
 * Time: 11:17:10
 * To change this template use File | Settings | File Templates.
 */
public class FresnelReflector<C extends Color> extends BRDF<C> {

    @Override
    public C f(final Shade sr, final Vector3D wo, final Vector3D wi){
        return null;
    }

    @Override
    public C rho(Shade sr, Vector3D wo) {
        return null;
    }

    @Override
    public Sample sampleF(Shade sr, Vector3D wo) {
        return null;
    }
}
