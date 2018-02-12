package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 17.04.2010
 * Time: 09:17:31
 * To change this template use File | Settings | File Templates.
 */
class Instance(val `object`: GeometricObject) : GeometricObject() {
    internal var transformTexture: Boolean = false
    internal var trans: AffineTransformation

    override// Transform these using the forward matrix
            // Compute the minimum values
            // Compute the minimum values
            // Assign values to the bounding box
    var boundingBox: BBox
        get() {
            val objectBbox = `object`.boundingBox

            val v = Array<Point3D>(8, { i-> Point3D.ORIGIN })

            val vx = DoubleArray(8)
            val vy = DoubleArray(8)
            val vz = DoubleArray(8)

            vx[0] = objectBbox.p!!.x
            vy[0] = objectBbox.p.y
            vz[0] = objectBbox.p.z
            vx[1] = objectBbox.q!!.x
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
                v[i] = trans.forwardMatrix.mult(p)
            }
            var x0 = MathUtils.K_HUGEVALUE
            var y0 = MathUtils.K_HUGEVALUE
            var z0 = MathUtils.K_HUGEVALUE

            for (j in 0..7) {
                if (v[j].x < x0)
                    x0 = v[j].x
            }

            for (j in 0..7) {
                if (v[j].y < y0)
                    y0 = v[j].y
            }

            for (j in 0..7) {
                if (v[j].z < z0)
                    z0 = v[j].z
            }

            var x1 = -MathUtils.K_HUGEVALUE
            var y1 = -MathUtils.K_HUGEVALUE
            var z1 = -MathUtils.K_HUGEVALUE

            for (j in 0..7) {
                if (v[j].x > x1)
                    x1 = v[j].x
            }

            for (j in 0..7) {
                if (v[j].y > y1)
                    y1 = v[j].y
            }

            for (j in 0..7) {
                if (v[j].z > z1)
                    z1 = v[j].z
            }
            return BBox(Point3D(x0, y0, z0), Point3D(x1, y1, z1))
        }
        set(value: BBox) {
            super.boundingBox = value
        }

    init {
        this.trans = AffineTransformation()
    }

    fun translate(v: Vector3D) {
        trans.translate(v)
    }

    fun translate(x: Double, y: Double, z: Double) {
        trans.translate(x, y, z)
    }

    fun scale(v: Vector3D) {
        trans.scale(v)
    }

    fun scale(x: Double, y: Double, z: Double) {
        trans.scale(x, y, z)
    }

    fun rotateX(phi: Double) {
        trans.rotateX(phi)
    }

    fun rotateY(phi: Double) {
        trans.rotateY(phi)
    }

    fun rotateZ(phi: Double) {
        trans.rotateZ(phi)
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val ro = trans.invMatrix.mult(ray.origin)
        val rd = trans.invMatrix.mult(ray.direction)
        val invRay = Ray(ro, rd)
        if (`object`.hit(invRay, sr)) {
            // TODO: Instance hit?
            val tmp = trans.invMatrix.mult(sr.normal)
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
        val ro = trans.invMatrix.mult(ray.origin)
        val rd = trans.invMatrix.mult(ray.direction)
        val invRay = Ray(ro, rd)
        return if (`object`.shadowHit(invRay, tmin)) {
            true
        } else false
    }

    override fun initialize() {
        `object`.initialize()
    }

}
