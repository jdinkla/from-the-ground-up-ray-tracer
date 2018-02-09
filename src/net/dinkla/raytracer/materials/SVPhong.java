package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.brdf.SVGlossySpecular;
import net.dinkla.raytracer.colors.ColorAccumulator;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.lights.AreaLight;
import net.dinkla.raytracer.lights.Light;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.textures.Texture;
import net.dinkla.raytracer.worlds.World;

import java.util.List;

public class SVPhong extends SVMatte {

    public SVGlossySpecular specularBrdf;

    public SVPhong() {
        super();
        specularBrdf = new SVGlossySpecular();
    }

    public void setKs(double ks) {
        specularBrdf.setKs(ks);
    }

    public void setExp(double exp) {
        specularBrdf.setExp(exp);
    }

    public void setCs(final Texture cs) {
        specularBrdf.setCs(cs);
    }

    @Override
    public Color shade(World world, Shade sr) {
        Vector3D wo = sr.ray.getD().negate();
        Color L = getAmbientColor(world, sr, wo);
        for (Light light : world.getLights()) {
            Vector3D wi = light.getDirection(sr);
            double nDotWi = sr.getNormal().dot(wi);
            if (nDotWi > 0) {
                boolean inShadow = false;
                if (light.getShadows()) {
                    Ray shadowRay = new Ray(sr.getHitPoint(), wi);
                    inShadow = light.inShadow(world, shadowRay, sr);
                }
                if (!inShadow) {
                    Color fd = diffuseBrdf.f(sr, wo, wi);
                    Color fs = specularBrdf.f(sr, wo, wi);
                    Color l = light.L(world, sr);
                    Color fdfslndotwi = fd.plus(fs).mult(l).mult(nDotWi);
                    L =  L.plus(fdfslndotwi);
                }
            }
        }
        return L;
    }


    @Override
    public Color areaLightShade(World world, Shade sr) {
        Vector3D wo = sr.ray.getD().negate();
        Color L = getAmbientColor(world, sr, wo);
        ColorAccumulator S = new ColorAccumulator();
        for (Light light1 : world.getLights()) {
            if (light1 instanceof AreaLight) {
                AreaLight light = (AreaLight) light1;
                List<AreaLight.Sample> ls = light.getSamples(sr);
                for (AreaLight.Sample sample : ls) {
                    double nDotWi = sample.wi.dot(sr.getNormal());
                    if (nDotWi > 0) {
                        boolean inShadow = false;
                        if (light.getShadows()) {
                            Ray shadowRay = new Ray(sr.getHitPoint(), sample.wi);
                            inShadow = light.inShadow(world, shadowRay, sr, sample);
                        }
                        if (!inShadow) {
                            Color fd = diffuseBrdf.f(sr, wo, sample.wi);
                            Color fs = specularBrdf.f(sr, wo, sample.wi);
                            Color l = light.L(world, sr, sample);
                            Color fsfslndotwi = fd.plus(fs).mult(l).mult(nDotWi);
                            // TODO: hier ist der Unterschied zu shade()
                            double f1 = light.G(sr, sample) / light.pdf(sr);
                            Color T = fsfslndotwi.mult(f1);
                            S.plus(T);
                        }
                    }
                }
            }
        }
        L =  L.plus(S.getAverage());
        return L;
    }

    @Override
    public Color getLe(Shade sr) {
        // TODO
        return  specularBrdf.getCs().getColor(sr).mult(specularBrdf.getKs());
    }
}

