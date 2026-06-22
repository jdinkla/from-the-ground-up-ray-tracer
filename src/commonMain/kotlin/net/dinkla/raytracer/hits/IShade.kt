package net.dinkla.raytracer.hits

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray

interface IShade : IHit {
    var ray: Ray
    val hitPoint: Point3D
    var depth: Int
    val material: IMaterial?

    /**
     * The hit point in the object's local coordinate system, used by texture mappings to compute
     * texture coordinates. For untransformed objects this equals [hitPoint]; an [Instance] applies
     * its inverse transform here. Defaults to [hitPoint] so existing [IShade] implementations stay
     * source-compatible (additive change, see TASK-18.1).
     */
    val localHitPoint: Point3D
        get() = hitPoint

    /** Texture coordinate u in [0,1], set by objects that compute parametric UVs. Defaults to 0. */
    val u: Double
        get() = 0.0

    /** Texture coordinate v in [0,1], set by objects that compute parametric UVs. Defaults to 0. */
    val v: Double
        get() = 0.0
}
