package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.math.PointUtilities.maximum
import net.dinkla.raytracer.math.PointUtilities.minimum

class Instance(val `object`: GeometricObject,
               private val trans: ITransformation) : GeometricObject(), ITransformation by trans {
//    internal var transformTexture: Boolean = false

    constructor (`object`: GeometricObject) : this(`object`, AffineTransformation()) {}

    override var boundingBox: BBox
        get() {
            val objectBbox = `object`.boundingBox

            objectBbox.p!!
            objectBbox.q!!

            val v = Array<Point3D>(8, { _ -> Point3D.ORIGIN })

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
            var (x0, y0, z0) = minimum(v, 8)

            var (x1, y1, z1) = maximum(v, 8)
            return BBox(Point3D(x0, y0, z0), Point3D(x1, y1, z1))
        }
        set(value: BBox) {
            super.boundingBox = value
        }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val ro = trans.invMatrix * ray.origin
        val rd = trans.invMatrix * ray.direction
        val invRay = Ray(ro, rd)
        if (`object`.hit(invRay, sr)) {
            // TODO: Instance hit?
            val tmp = trans.invMatrix * sr.normal
            sr.normal = tmp.normalize()
            if (null != `object`.material) {
                sr.`object` = `object`
            }
            //            if (!transformTexture) {
            //            }
            return true
        }
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val ro = trans.invMatrix * ray.origin
        val rd = trans.invMatrix * ray.direction
        val invRay = Ray(ro, rd)
        return `object`.shadowHit(invRay, tmin)
    }

    override fun initialize() = `object`.initialize()

}


