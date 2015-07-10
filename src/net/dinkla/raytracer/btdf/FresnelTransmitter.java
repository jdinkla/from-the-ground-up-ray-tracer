package net.dinkla.raytracer.btdf;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Vector3DF;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 15.05.2010
 * Time: 11:19:51
 * To change this template use File | Settings | File Templates.
 */
public class FresnelTransmitter<C extends Color> extends BTDF<C> {
    
    @Override
    public C f(Shade sr, Vector3DF wo, Vector3DF wi) {
        return null;
    }

    @Override
    public boolean isTir(Shade sr) {
        return false;
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
