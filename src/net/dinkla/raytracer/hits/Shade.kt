package net.dinkla.raytracer.hits

import net.dinkla.raytracer.materials.Material
import net.dinkla.raytracer.math.*

/**
 *
 * Typical lifecycle
 *
 * <pre>
 * Shade sr = world.getAccelerator().hitObjects(world, ray);
 * if (sr.hitsAnObject) {
 * RGBColor color = sr.material.shade(sr); ...
</pre> *
 *
 */
class Shade : Hit() {

    // for specular highlights, set by Tracer
    var ray: Ray

    // Recursion depth, set by tracer
    var depth: Int = 0

    val hitPoint: Point3D
        get() = ray.linear(t)

    val localHitPoint: Point3D
        get() = ray.linear(t)

    val material: Material?
        get() = `object`!!.material

    init {
        this.depth = 0
        this.ray = Ray.DEFAULT
        this.`object` = null
    }

}
