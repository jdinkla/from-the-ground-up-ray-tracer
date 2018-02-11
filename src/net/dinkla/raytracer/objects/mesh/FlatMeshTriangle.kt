package net.dinkla.raytracer.objects.mesh

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.objects.mesh.MeshTriangle

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 16.05.2010
 * Time: 10:03:16
 * To change this template use File | Settings | File Templates.
 */
class FlatMeshTriangle : MeshTriangle {

    constructor(mesh: Mesh) : super(mesh) {}

    constructor(mesh: Mesh, i0: Int, i1: Int, i2: Int) : super(mesh, i0, i1, i2) {}

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val v0 = mesh.vertices[index0]
        val v1 = mesh.vertices[index1]
        val v2 = mesh.vertices[index2]

        val a = v0.x - v1.x
        val b = v0.x - v2.x
        val c = ray.d.x
        val d = v0.x - ray.o.x
        val e = v0.y - v1.y
        val f = v0.y - v2.y
        val g = ray.d.y
        val h = v0.y - ray.o.y
        val i = v0.z - v1.z
        val j = v0.z - v2.z
        val k = ray.d.z
        val l = v0.z - ray.o.z

        val m = f * k - g * j
        val n = h * k - g * l
        val p = f * l - h * j
        val q = g * i - e * k
        val s = e * j - f * i

        val invDenom = 1.0 / (a * m + b * q + c * s)

        val e1 = d * m - b * n - c * p
        val beta = e1 * invDenom

        if (beta < 0.0)
            return false

        val r = e * l - h * i
        val e2 = a * n + d * q + c * r
        val gamma = e2 * invDenom

        if (gamma < 0.0)
            return false

        if (beta + gamma > 1.0)
            return false

        val e3 = a * p - b * r + d * s
        val t = e3 * invDenom

        if (t < MathUtils.K_EPSILON)
            return false

        sr.t = t
        assert(normal != null)
        sr.normal = normal ?: Normal.ZERO

        return true
    }

}