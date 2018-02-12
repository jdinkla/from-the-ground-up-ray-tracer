package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.brdf.Lambertian
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.worlds.World

open class Matte : Material {

    var ambientBrdf: Lambertian
    var diffuseBrdf: Lambertian

    constructor() {
        ambientBrdf = Lambertian()
        diffuseBrdf = Lambertian()
        setKa(0.25)
        setKd(0.75)
        setCd(Color.WHITE)
    }

    constructor(color: Color, ka: Double, kd: Double) {
        ambientBrdf = Lambertian()
        diffuseBrdf = Lambertian()
        setKa(ka)
        setKd(kd)
        setCd(color)
    }

    fun setKa(ka: Double) {
        ambientBrdf.kd = ka
    }

    fun setKd(kd: Double) {
        diffuseBrdf.kd = kd
    }

    fun setCd(cd: Color) {
        ambientBrdf.cd = cd
        diffuseBrdf.cd = cd
    }

    override fun shade(world: World, sr: Shade): Color {
        val wo = sr.ray.direction.negate()
        var L = getAmbientColor(world, sr, wo)
        for (light in world.lights) {
            val wi = light.getDirection(sr)
            val nDotWi = wi.dot(sr.normal)
            if (nDotWi > 0) {
                var inShadow = false
                if (light.shadows) {
                    val shadowRay = Ray(sr.hitPoint, wi)
                    inShadow = light.inShadow(world, shadowRay, sr)
                }
                if (!inShadow) {
                    val f = diffuseBrdf.f(sr, wo, wi)
                    val l = light.L(world, sr)
                    val flndotwi = f.times(l).times(nDotWi)
                    L = L.plus(flndotwi)
                }
            }
        }
        return L
    }

    /*
    	Vector3D 	wo 			= -sr.ray.direction;
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
    override fun areaLightShade(world: World, sr: Shade): Color {
        val wo = sr.ray.direction.negate()
        var L = getAmbientColor(world, sr, wo)
        val S = ColorAccumulator()
        for (light1 in world.lights) {
            if (light1 is AreaLight) {
                val ls = light1.getSamples(sr)
                for (sample in ls) {
                    val nDotWi = sample.wi!!.dot(sr.normal)
                    if (nDotWi > 0) {
                        var inShadow = false
                        if (light1.shadows) {
                            val shadowRay = Ray(sr.hitPoint, sample.wi!!)
                            inShadow = light1.inShadow(world, shadowRay, sr, sample)
                        }
                        if (!inShadow) {
                            val f = diffuseBrdf.f(sr, wo, sample.wi!!)
                            val l = light1.L(world, sr, sample)
                            val flndotwi = f.times(l).times(nDotWi)
                            // TODO: hier ist der Unterschied zu shade()
                            val f1 = light1.G(sr, sample) / light1.pdf(sr)
                            val T = flndotwi.times(f1)
                            S.plus(T)
                        }
                    }
                }
            }
        }
        L = L.plus(S.average)
        return L
    }

    protected fun getAmbientColor(world: World, sr: Shade, wo: Vector3D): Color {
        val c1 = ambientBrdf.rho(sr, wo)
        val c2 = world.ambientLight.L(world, sr)
        return c1.times(c2)
    }

    override fun getLe(sr: Shade): Color {
        return diffuseBrdf.rho(sr, null!!)
    }

    companion object {

        var materials = arrayOf(Matte(Color(0.0, 0.0, 1.0), 1.0, 1.0), Matte(Color(0.0, 1.0, 1.0), 1.0, 1.0), Matte(Color(1.0, 1.0, 0.0), 1.0, 1.0), Matte(Color(0.0, 1.0, 0.0), 1.0, 1.0), Matte(Color(1.0, 0.0, 0.0), 1.0, 1.0), Matte(Color(1.0, 0.0, 1.0), 1.0, 1.0), Matte(Color(1.0, 1.0, 1.0), 1.0, 1.0))
    }
}

