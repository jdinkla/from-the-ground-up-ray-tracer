package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 14.04.2010
 * Time: 21:10:53
 * To change this template use File | Settings | File Templates.
 */
public class Emissive<C extends Color> extends Material<C> {

    public float ls;
    public C ce;

    protected C cachedLe;

    public Emissive() {
        ls = 1.0f;
        ce = (C) C.getWhite();
    }
   
    public Emissive(C ce, float ls) {
        this.ce = ce;
        this.ls = ls;
    }

    @Override
    public C shade(World world, Shade sr) {
        throw new RuntimeException("Emissive.shade");
    }
    
    @Override
    public C areaLightShade(World world, Shade sr) {
        if (sr.getNormal().negate().dot(sr.ray.d) > 0) {
            return getLe(sr);
        } else {
            return (C) C.getBlack();
        }
    }

    @Override
    public C getLe(Shade sr) {
        if (null == cachedLe) {
            cachedLe = (C) ce.mult(ls);
        }
        return cachedLe;
    }
    
}
