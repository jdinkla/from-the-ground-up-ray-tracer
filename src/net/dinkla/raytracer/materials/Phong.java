package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.ColorAccumulator;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.brdf.GlossySpecular;
import net.dinkla.raytracer.lights.AreaLight;
import net.dinkla.raytracer.lights.Light;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.worlds.World;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 15:13:47
 * To change this template use File | Settings | File Templates.
 */
public class Phong<C extends Color> extends Matte<C> {

    public GlossySpecular<C> specularBrdf;
    
    public Phong() {
        super();
        specularBrdf = new GlossySpecular<C>();
    }

    public void setKs(float ks) {
        specularBrdf.ks = ks;
    }

    public void setExp(float exp) {
        specularBrdf.exp = exp;
    }

    public void setCs(final C cs) {
        specularBrdf.cs = cs;
    }

    @Override
    public C shade(World<C> world, Shade sr) {
        Vector3DF wo = sr.ray.getD().negate();
        C L = getAmbientColor(world, sr, wo);
        for (Light light : world.getLights()) {
            Vector3DF wi = light.getDirection(sr);
            float nDotWi = sr.getNormal().dot(wi);
            if (nDotWi > 0) {
                boolean inShadow = false;
                if (light.shadows) {
                    Ray shadowRay = new Ray(sr.getHitPoint(), wi);
                    inShadow = light.inShadow(world, shadowRay, sr);
                }
                if (!inShadow) {
                    Color fd = diffuseBrdf.f(sr, wo, wi);
                    Color fs = specularBrdf.f(sr, wo, wi);
                    Color l = light.L(world, sr);
                    Color fdfslndotwi = fd.plus(fs).mult(l).mult(nDotWi);
                    L = (C) L.plus(fdfslndotwi);
                }
            }
        }
        return L;
    }


    @Override
    public C areaLightShade(World<C> world, Shade sr) {
        Vector3DF wo = sr.ray.getD().negate();
        C L = getAmbientColor(world, sr, wo);
        ColorAccumulator<C> S = new ColorAccumulator<C>();
        for (Light light1 : world.getLights()) {
            if (light1 instanceof AreaLight) {
                AreaLight light = (AreaLight) light1;
                List<AreaLight.Sample> ls = light.getSamples(sr);
                for (AreaLight.Sample sample : ls) {
                    float nDotWi = sample.wi.dot(sr.getNormal());
                    if (nDotWi > 0) {
                        boolean inShadow = false;
                        if (light.shadows) {
                            Ray shadowRay = new Ray(sr.getHitPoint(), sample.wi);
                            inShadow = light.inShadow(world, shadowRay, sr, sample);
                        }
                        if (!inShadow) {                            
                            Color fd = diffuseBrdf.f(sr, wo, sample.wi);
                            Color fs = specularBrdf.f(sr, wo, sample.wi);
                            Color l = light.L(world, sr, sample);
                            Color fsfslndotwi = fd.plus(fs).mult(l).mult(nDotWi);
                            // TODO: hier ist der Unterschied zu shade()
                            float f1 = light.G(sr, sample) / light.pdf(sr);
                            Color T = fsfslndotwi.mult(f1);
                            S.plus(T);
                        }
                    }
                }
            }
        }
        L = (C) L.plus(S.getAverage());
        return L;
    }

    @Override
    public C getLe(Shade sr) {
        // TODO
        return (C) specularBrdf.cs.mult(specularBrdf.ks);
    }
}