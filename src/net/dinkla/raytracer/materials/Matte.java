package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.ColorAccumulator;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.brdf.Lambertian;
import net.dinkla.raytracer.lights.AreaLight;
import net.dinkla.raytracer.lights.Light;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 15:13:47
 * To change this template use File | Settings | File Templates.
 */
public class Matte<C extends Color> extends Material<C> {

    public static Matte[] materials = {
            new Matte(new RGBColor(0.0f, 0.0f, 1.0f), 1.0f, 1.0f),
            new Matte(new RGBColor(0.0f, 1.0f, 1.0f), 1.0f, 1.0f),
            new Matte(new RGBColor(1.0f, 1.0f, 0.0f), 1.0f, 1.0f),
            new Matte(new RGBColor(0.0f, 1.0f, 0.0f), 1.0f, 1.0f),
            new Matte(new RGBColor(1.0f, 0.0f, 0.0f), 1.0f, 1.0f),
            new Matte(new RGBColor(1.0f, 0.0f, 1.0f), 1.0f, 1.0f),
            new Matte(new RGBColor(1.0f, 1.0f, 1.0f), 1.0f, 1.0f)
            };
    
    public Lambertian<C> ambientBrdf;
    public Lambertian<C> diffuseBrdf;

    public Matte() {
        ambientBrdf = new Lambertian<C>();
        diffuseBrdf = new Lambertian<C>();
        setKa(0.25f);
        setKd(0.75f);
        setCd((C) C.getWhite());
    }

    public Matte(final C color, final float ka, final float kd) {
        ambientBrdf = new Lambertian<C>();
        diffuseBrdf = new Lambertian<C>();
        setKa(ka);
        setKd(kd);
        setCd(color);
    }

    public void setKa(float ka) {
        ambientBrdf.kd = ka;
    }

    public void setKd(float kd) {
        diffuseBrdf.kd = kd;
    }

    public void setCd(final C cd) {
        ambientBrdf.cd = cd;
        diffuseBrdf.cd = cd;
    }

    @Override
    public C shade(World<C> world, Shade sr) {
        Vector3D wo = sr.ray.d.negate();
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

    /*
    	Vector3D 	wo 			= -sr.ray.d;
	RGBColor 	L 			= ambient_brdf->rho(sr, wo) * sr.w.ambient_ptr->L(sr);
	int 		num_lights	= sr.w.lights.size();

	for (int j = 0; j < num_lights; j++) {
		Vector3D wi = sr.w.lights[j]->get_direction(sr);
		float ndotwi = sr.normal * wi;

		if (ndotwi > 0.0)
			L += diffuse_brdf->f(sr, wo, wi) * sr.w.lights[j]->L(sr) * ndotwi;
	}

	return (L);
    */
    @Override
    public C areaLightShade(World<C> world, Shade sr) {
        Vector3D wo = sr.ray.d.negate();
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
        final C c1 = ambientBrdf.rho(sr, wo);
        final C c2 = (C) world.getAmbientLight().L(world, sr);
        final C L = (C) c1.mult(c2);
        return L;
    }

    @Override
    public C getLe(Shade sr) {
        return diffuseBrdf.rho(sr, null);
    }
}

