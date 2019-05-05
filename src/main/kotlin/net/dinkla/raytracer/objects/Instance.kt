package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.math.PointUtilities.maximum
import net.dinkla.raytracer.math.PointUtilities.minimum

class Instance(private val geometricObject: GeometricObject,
               val trans: Transformation = AffineTransformation()) : GeometricObject(), Transformation by trans {

    override var boundingBox: BBox
        get() {
            val objectBbox = geometricObject.boundingBox

            objectBbox.p
            objectBbox.q

            val v = Array(8) { _ -> Point3D.ORIGIN }

            val vx = DoubleArray(8)
            val vy = DoubleArray(8)
            val vz = DoubleArray(8)

            vx[0] = objectBbox.p.x
            vy[0] = objectBbox.p.y
            vz[0] = objectBbox.p.z
            vx[1] = objectBbox.q.x
            vy[1] = objectBbox.p.y
            vz[1] = objectBbox.p.z
            vx[2] = objectBbox.q.x
            vy[2] = objectBbox.q.y
            vz[2] = objectBbox.p.z
            vx[3] = objectBbox.p.x
            vy[3] = objectBbox.q.y
            vz[3] = objectBbox.p.z

            vx[4] = objectBbox.p.x
            vy[4] = objectBbox.p.y
            vz[4] = objectBbox.q.z
            vx[5] = objectBbox.q.x
            vy[5] = objectBbox.p.y
            vz[5] = objectBbox.q.z
            vx[6] = objectBbox.q.x
            vy[6] = objectBbox.q.y
            vz[6] = objectBbox.q.z
            vx[7] = objectBbox.p.x
            vy[7] = objectBbox.q.y
            vz[7] = objectBbox.q.z
            for (i in 0..7) {
                val p = Point3D(vx[i], vy[i], vz[i])
                v[i] = p
                v[i] = trans.forwardMatrix * p
            }
            val (x0, y0, z0) = minimum(v, 8)

            val (x1, y1, z1) = maximum(v, 8)
            return BBox(Point3D(x0, y0, z0), Point3D(x1, y1, z1))
        }
        set(value) {
            super.boundingBox = value
        }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val invRay = ray(ray)
        if (geometricObject.hit(invRay, sr)) {
            // TODO: Instance hit?
            val tmp = trans.invMatrix * sr.normal
            sr.normal = tmp.normalize()
            if (null != geometricObject.material) {
                sr.`object` = geometricObject
            }
            //            if (!transformTexture) {
            //            }
            return true
        }
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        return geometricObject.shadowHit(ray(ray), tmin)
    }

    override fun initialize() = geometricObject.initialize()

}


