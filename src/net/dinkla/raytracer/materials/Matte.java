package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.ColorAccumulator;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.brdf.Lambertian;
import net.dinkla.raytracer.lights.AreaLight;
import net.dinkla.raytracer.lights.Light;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

import java.util.List;

public class Matte extends Material {

    public static Matte[] materials = {
            new Matte(new Color(0.0, 0.0, 1.0), 1.0, 1.0),
            new Matte(new Color(0.0, 1.0, 1.0), 1.0, 1.0),
            new Matte(new Color(1.0, 1.0, 0.0), 1.0, 1.0),
            new Matte(new Color(0.0, 1.0, 0.0), 1.0, 1.0),
            new Matte(new Color(1.0, 0.0, 0.0), 1.0, 1.0),
            new Matte(new Color(1.0, 0.0, 1.0), 1.0, 1.0),
            new Matte(new Color(1.0, 1.0, 1.0), 1.0, 1.0)
            };
    
    public Lambertian ambientBrdf;
    public Lambertian diffuseBrdf;

    public Matte() {
        ambientBrdf = new Lambertian();
        diffuseBrdf = new Lambertian();
        setKa(0.25);
        setKd(0.75);
        setCd(Color.WHITE);
    }

    public Matte(final Color color, final double ka, final double kd) {
        ambientBrdf = new Lambertian();
        diffuseBrdf = new Lambertian();
        setKa(ka);
        setKd(kd);
        setCd(color);
    }

    public void setKa(double ka) {
        ambientBrdf.setKd(ka);
    }

    public void setKd(double kd) {
        diffuseBrdf.setKd(kd);
    }

    public void setCd(final Color cd) {
        ambientBrdf.setCd(cd);
        diffuseBrdf.setCd(cd);
    }

    @Override
    public Color shade(World world, Shade sr) {
        Vector3D wo = sr.ray.getD().negate();
        Color L = getAmbientColor(world, sr, wo);
        for (Light light : world.getLights()) {
            Vector3D wi = light.getDirection(sr);
            double nDotWi = wi.dot(sr.getNormal());
            if (nDotWi > 0) {
                boolean inShadow = false;
                if (light.getShadows()) {
                    Ray shadowRay = new Ray(sr.getHitPoint(), wi);
                    inShadow = light.inShadow(world, shadowRay, sr);
                }
                if (!inShadow) {
                    Color f = diffuseBrdf.f(sr, wo, wi);
                    Color l = light.L(world, sr);
                    Color flndotwi = f.mult(l).mult(nDotWi);
                    L = L.plus(flndotwi);
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
		double ndotwi = sr.normal * wi;

		if (ndotwi > 0.0)
			L += diffuse_brdf->f(sr, wo, wi) * sr.w.lights[j]->L(sr) * ndotwi;
	}

	return (L);
    */
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
                    double nDotWi = sample.getWi().dot(sr.getNormal());
                    if (nDotWi > 0) {
                        boolean inShadow = false;
                        if (light.getShadows()) {
                            Ray shadowRay = new Ray(sr.getHitPoint(), sample.getWi());
                            inShadow = light.inShadow(world, shadowRay, sr, sample);
                        }
                        if (!inShadow) {
                            Color f = diffuseBrdf.f(sr, wo, sample.getWi());
                            Color l = light.L(world, sr, sample);
                            Color flndotwi = f.mult(l).mult(nDotWi);
                            // TODO: hier ist der Unterschied zu shade()
                            double f1 = light.G(sr, sample) / light.pdf(sr);
                            Color T = flndotwi.mult(f1);
                            S.plus(T);
                        }
                    }
                }
            }
        }
        L = L.plus(S.getAverage());
        return L;
    }

    protected Color getAmbientColor(World world, Shade sr, Vector3D wo) {
        final Color c1 = ambientBrdf.rho(sr, wo);
        final Color c2 = world.getAmbientLight().L(world, sr);
        final Color L = c1.mult(c2);
        return L;
    }

    @Override
    public Color getLe(Shade sr) {
        return diffuseBrdf.rho(sr, null);
    }
}

