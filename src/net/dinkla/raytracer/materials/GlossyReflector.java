package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.brdf.BRDF;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.brdf.GlossySpecular;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.samplers.Sampler;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 25.04.2010
 * Time: 17:14:03
 * To change this template use File | Settings | File Templates.
 */
public class GlossyReflector extends Phong {

    protected final GlossySpecular glossySpecularBrdf;

    public GlossyReflector() {
        super();
        glossySpecularBrdf = new GlossySpecular();
    }

    public void setKr(final float kr) {
        glossySpecularBrdf.ks = kr;
    }

    public void setExp(final float exp) {
        glossySpecularBrdf.exp = exp;
    }

    @Override
    public Color areaLightShade(World world, Shade sr) {
        Color L = super.areaLightShade(world, sr);
        final Vector3D wo = sr.ray.getD().negate();
        final BRDF.Sample result = glossySpecularBrdf.sampleF(sr, wo);
        final Ray reflectedRay = new Ray(sr.getHitPoint(), result.wi);
        final Color r = world.getTracer().trace(reflectedRay, sr.depth +1);
        final Color r2 = result.color.mult(r);
        final Color r3 = r2.mult(result.wi.dot(sr.getNormal()) / result.pdf);
        return result.color;
//        L = L.plus(r);
//        return L;
    }

    public void setSampler(Sampler sampler) {
        glossySpecularBrdf.sampler = sampler;
    }
}
