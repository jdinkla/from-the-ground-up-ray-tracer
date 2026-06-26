package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D

/**
 * The bihemispherical-reflectance (albedo) role of a BRDF: [rho] is the fraction of incident light
 * reflected over the hemisphere, used by the ambient term and emissive `getLe`. Only the diffuse
 * BRDFs (`Lambertian`, `SvLambertian`) have a meaningful [rho]; specular and glossy BRDFs do not and
 * therefore do not implement this interface. Split out per TASK-63.
 */
interface ReflectanceBRDF {
    fun rho(
        sr: IShade,
        wo: Vector3D,
    ): Color
}
