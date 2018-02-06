package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.brdf.SVLambertian;
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

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 22:01:44
 * To change this template use File | Settings | File Templates.
 */
public class SVMatte <C extends Color> extends Material<C> {

    public SVLambertian<C> ambientBrdf;
    public SVLambertian<C> diffuseBrdf;

    public SVMatte() {
        ambientBrdf = new SVLambertian<C>();
        diffuseBrdf = new SVLambertian<C>();
        setKa(0.25f);
        setKd(0.75f);
        setCd(null);
    }

    public SVMatte(final Texture<C> color, final float ka, final float kd) {
        ambientBrdf = new SVLambertian<C>();
        diffuseBrdf = new SVLambertian<C>();
        setKa(ka);
        setKd(kd);
        setCd(color);
    }

    public void setKa(float ka) {
        ambientBrdf.setKd(ka);
    }

    public void setKd(float kd) {
        diffuseBrdf.setKd(kd);
    }

    public void setCd(final Texture<C> cd) {
        ambientBrdf.setCd(cd);
        diffuseBrdf.setCd(cd);
    }

    @Override
    public C shade(World<C> world, Shade sr) {
        Vector3D wo = sr.ray.getD().negate();
        C L = getAmbientColor(world, sr, wo);
        for (Light light : world.getLights()) {
            Vector3D wi = light.getDirection(sr);
            float nDotWi = wi.dot(sr.getNormal());
            if (nDotWi > 0) {
                boolean inShadow = false;
                if (light.shadows) {
                    Ray shadowRay = new Ray(sr.getHitPoint(), wi);
                    inShadow = light.inShadow(world, shadowRay, sr);
                }
                if (!inShadow) {
                    Color f = diffuseBrdf.f(sr, wo, wi);
                    Color l = light.L(world, sr);
                    Color flndotwi = f.mult(l).mult(nDotWi);
                    L = (C) L.plus(flndotwi);
                }
            }
        }
        return L;
    }

    @Override
    public C areaLightShade(World<C> world, Shade sr) {
        Vector3D wo = sr.ray.getD().negate();
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
                            Color f = diffuseBrdf.f(sr, wo, sample.wi);
                            Color l = light.L(world, sr, sample);
                            Color flndotwi = f.mult(l).mult(nDotWi);
                            // TODO: hier ist der Unterschied zu shade()
                            float f1 = light.G(sr, sample) / light.pdf(sr);
                            C T = (C) flndotwi.mult(f1);
                            S.plus(T);
                        }
                    }
                }
            }
        }
        L = (C) L.plus(S.getAverage());
        return L;
    }

    protected C getAmbientColor(World<C> world, Shade sr, Vector3D wo) {
        C c1 = ambientBrdf.rho(sr, wo);
        C c2 = (C) world.getAmbientLight().L(world, sr);
        C L = (C) c1.mult(c2);
        return L;
    }

    @Override
    public C getLe(Shade sr) {
        return diffuseBrdf.rho(sr, null);
    }
}
