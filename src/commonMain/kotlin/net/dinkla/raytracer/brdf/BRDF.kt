package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D

/**
 * The evaluation role of a bidirectional reflectance distribution function: given an incoming and
 * outgoing direction at a hit, return the reflectance [f]. This is the role direct-lighting shading
 * (`Matte`, `Phong`, their spatially-varying variants) needs.
 *
 * It is deliberately split from the sampling role ([SamplingBRDF.sampleF]) and the bihemispherical
 * reflectance role ([ReflectanceBRDF.rho]): perfectly specular BRDFs (`PerfectSpecular`,
 * `FresnelReflector`) have no evaluable [f] (it is a delta function) and therefore do **not**
 * implement this interface — they are only [SamplingBRDF]s. See TASK-63 for the interface-segregation
 * rationale.
 */
interface BRDF {
    fun f(
        sr: IShade,
        wo: Vector3D,
        wi: Vector3D,
    ): Color
}
