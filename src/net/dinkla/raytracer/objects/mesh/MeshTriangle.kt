package net.dinkla.raytracer.objects.mesh

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.utilities.Counter

open class MeshTriangle : GeometricObject {

    /*
    double interpolateU(final double beta, final double gamma) {
        return ((1 - beta - gamma) * mesh.us.get(index0)
                + beta * mesh.us.get(index1)
                + gamma * mesh.us.get(index2));
    }

    double interpolateV(final double beta, final double gamma) {
        return ((1 - beta - gamma) * mesh.vs.get(index0)
                + beta * mesh.vs.get(index1)
                + gamma * mesh.vs.get(index2));
    }
    */

    var mesh: Mesh
    internal var index0: Int = 0
    internal var index1: Int = 0
    internal var index2: Int = 0
    var normal: Normal? = null
        internal set
    //double area;

    override var boundingBox: BBox
        get() = calcBBox()
        set(value: BBox) {
            super.boundingBox = value
        }

    constructor(mesh: Mesh) {
        this.mesh = mesh
        index0 = 0
        index1 = 0
        index2 = 0
        normal = null
        //area = 0;
    }

    constructor(mesh: Mesh, i0: Int, i1: Int, i2: Int) {
        this.mesh = mesh
        this.index0 = i0
        this.index1 = i1
        this.index2 = i2
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val p0 = mesh.vertices[index0]
        val p1 = mesh.vertices[index1]
        val p2 = mesh.vertices[index2]

        val a = p0.x - p1.x
        val b = p0.x - p2.x
        val c = ray.d.x
        val d = p0.x - ray.o.x
        val e = p0.y - p1.y
        val f = p0.y - p2.y
        val g = ray.d.y
        val h = p0.y - ray.o.y
        val i = p0.z - p1.z
        val j = p0.z - p2.z
        val k = ray.d.z
        val l = p0.z - ray.o.z

        val m = f * k - g * j
        val n = h * k - g * l
        val p = f * l - h * j
        val q = g * i - e * k
        val s = e * j - f * i

        val invDenom = 1.0 / (a * m + b * q + c * s)

        val e1 = d * m - b * n - c * p
        val beta = e1 * invDenom

        if (beta < 0.0) {
            return false
        }

        val r = e * l - h * i
        val e2 = a * n + d * q + c * r
        val gamma = e2 * invDenom

        if (gamma < 0.0) {
            return false
        }
        if (beta + gamma > 1.0) {
            return false
        }
        val e3 = a * p - b * r + d * s
        val t = e3 * invDenom

        if (t < MathUtils.K_EPSILON) {
            return false
        }
        tmin.t = t
        return true
    }


    fun computeNormal(reverseNormal: Boolean) {
        val p0 = mesh.vertices[index0]
        val p1 = mesh.vertices[index1]
        val p2 = mesh.vertices[index2]
        //normal = new Normal(p0, p1, p2);
        normal = Normal(p1.minus(p0).cross(p2.minus(p0)).normalize())
        if (reverseNormal) {
            normal = normal!!.negate()
        }
    }

    fun calcBBox(): BBox {
        val p0 = mesh.vertices[index0]
        val p1 = mesh.vertices[index1]
        val p2 = mesh.vertices[index2]

        val min = MathUtils.minMin(p0, p1, p2).minus(MathUtils.K_EPSILON)
        val max = MathUtils.maxMax(p0, p1, p2).plus(MathUtils.K_EPSILON)
        return BBox(min, max)
    }
}
