package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.Logger

interface IGeometricObject {
    var isShadows: Boolean
    var boundingBox: BBox
    var material: IMaterial?

    fun initialize()

    fun hit(ray: Ray, sr: IHit): Boolean

    fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val t = shadowHit(ray)
        return when (t) {
            is Shadow.Hit -> {
                tmin.t = t.t
                true
            }

            Shadow.None -> {
                false
            }
        }
    }

    fun shadowHit(ray: Ray): Shadow {
        Logger.warn("Who is calling me?")
        val t = ShadowHit()
        return if (shadowHit(ray, t)) {
            Shadow.Hit(t.t)
        } else {
            Shadow.None
        }
    }

}
