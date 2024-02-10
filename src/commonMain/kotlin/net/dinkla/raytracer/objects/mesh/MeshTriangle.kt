package net.dinkla.raytracer.objects.mesh

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.GeometricObject

open class MeshTriangle : GeometricObject {
    var mesh: Mesh
    internal var index0: Int = 0
    internal var index1: Int = 0
    internal var index2: Int = 0
    var normal: Normal? = null
        internal set

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
    }

    constructor(mesh: Mesh, i0: Int, i1: Int, i2: Int) {
        this.mesh = mesh
        this.index0 = i0
        this.index1 = i1
        this.index2 = i2
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
        return false
    }

    override fun shadowHit(ray: Ray): Shadow {
        val p0 = mesh.vertices[index0]
        val p1 = mesh.vertices[index1]
        val p2 = mesh.vertices[index2]

        val a = p0.x - p1.x
        val b = p0.x - p2.x
        val c = ray.direction.x
        val d = p0.x - ray.origin.x
        val e = p0.y - p1.y
        val f = p0.y - p2.y
        val g = ray.direction.y
        val h = p0.y - ray.origin.y
        val i = p0.z - p1.z
        val j = p0.z - p2.z
        val k = ray.direction.z
        val l = p0.z - ray.origin.z

        val m = f * k - g * j
        val n = h * k - g * l
        val p = f * l - h * j
        val q = g * i - e * k
        val s = e * j - f * i

        val invDenom = 1.0 / (a * m + b * q + c * s)

        val e1 = d * m - b * n - c * p
        val beta = e1 * invDenom

        if (beta < 0.0) {
            return Shadow.None
        }

        val r = e * l - h * i
        val e2 = a * n + d * q + c * r
        val gamma = e2 * invDenom

        if (gamma < 0.0) {
            return Shadow.None
        }
        if (beta + gamma > 1.0) {
            return Shadow.None
        }
        val e3 = a * p - b * r + d * s
        val t = e3 * invDenom

        if (t < MathUtils.K_EPSILON) {
            return Shadow.None
        }
        return Shadow.Hit(t)
    }

    fun computeNormal(reverseNormal: Boolean) {
        val p0 = mesh.vertices[index0]
        val p1 = mesh.vertices[index1]
        val p2 = mesh.vertices[index2]
        normal = Normal.create((p1 - p0) cross (p2 - p0).normalize())
        if (reverseNormal) {
            normal = -normal!!
        }
    }

    private fun calcBBox(): BBox {
        val p0 = mesh.vertices[index0]
        val p1 = mesh.vertices[index1]
        val p2 = mesh.vertices[index2]

        val min = MathUtils.min(p0, p1, p2).minus(MathUtils.K_EPSILON)
        val max = MathUtils.maxMax(p0, p1, p2).plus(MathUtils.K_EPSILON)
        return BBox(min, max)
    }
}
