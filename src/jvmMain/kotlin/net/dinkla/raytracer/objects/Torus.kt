package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.utilities.hash

class Torus(val a: Double, val b: Double) : GeometricObject() {

    init {
        boundingBox = BBox(Point3D(-a - b, -b, -a - b), Point3D(a + b, b, a + b))
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        if (!boundingBox.hit(ray)) {
            return false
        }
        val x1 = ray.origin.x
        val y1 = ray.origin.y
        val z1 = ray.origin.z

        val d1 = ray.direction.x
        val d2 = ray.direction.y
        val d3 = ray.direction.z

        val coeffs = DoubleArray(5)
        val roots = DoubleArray(4)

        val sumDSqrd = d1 * d1 + d2 * d2 + d3 * d3
        val e = x1 * x1 + y1 * y1 + z1 * z1 - a * a - b * b
        val f = x1 * d1 + y1 * d2 + z1 * d3
        val fourASqrd = 4.0 * a * a

        coeffs[0] = e * e - fourASqrd * (b * b - y1 * y1)
        coeffs[1] = 4.0 * f * e + 2.0 * fourASqrd * y1 * d2
        coeffs[2] = 2.0 * sumDSqrd * e + 4.0 * f * f + fourASqrd * d2 * d2
        coeffs[3] = 4.0 * sumDSqrd * f
        coeffs[4] = sumDSqrd * sumDSqrd

        val numRealRoots = Polynomials.solveQuartic(coeffs, roots)
        var intersected = false
        var t = java.lang.Double.MAX_VALUE

        if (numRealRoots == 0) {
            return false
        }

        for (j in 0 until numRealRoots) {
            if (roots[j] > MathUtils.K_EPSILON) {
                intersected = true
                if (roots[j] < t) {
                    t = roots[j]
                }
            }
        }
        if (!intersected) {
            return false
        }
        sr.t = t
        sr.normal = computeNormal(ray.linear(t))
        return true
    }

    fun hitF(ray: Ray, sr: Hit): Boolean {
        if (!boundingBox.hit(ray)) {
            return false
        }
        val x1 = ray.origin.x
        val y1 = ray.origin.y
        val z1 = ray.origin.z

        val d1 = ray.direction.x
        val d2 = ray.direction.y
        val d3 = ray.direction.z

        val coeffs = DoubleArray(5)
        val roots = DoubleArray(4)

        val sumDSqrd = d1 * d1 + d2 * d2 + d3 * d3
        val e = x1 * x1 + y1 * y1 + z1 * z1 - a * a - b * b
        val f = x1 * d1 + y1 * d2 + z1 * d3
        val fourASqrd = 4.0 * a * a

        coeffs[0] = e * e - fourASqrd * (b * b - y1 * y1)
        coeffs[1] = 4.0 * f * e + 2.0 * fourASqrd * y1 * d2
        coeffs[2] = 2.0 * sumDSqrd * e + 4.0 * f * f + fourASqrd * d2 * d2
        coeffs[3] = 4.0 * sumDSqrd * f
        coeffs[4] = sumDSqrd * sumDSqrd

        val numRealRoots = Polynomials.solveQuartic(coeffs, roots)
        var intersected = false
        var t = java.lang.Double.MAX_VALUE

        if (numRealRoots == 0) {
            return false
        }

        for (j in 0 until numRealRoots) {
            if (roots[j] > MathUtils.K_EPSILON) {
                intersected = true
                if (roots[j] < t) {
                    t = roots[j]
                }
            }
        }
        if (!intersected) {
            return false
        }
        sr.t = t
        sr.normal = computeNormal(ray.linear(t))
        return true
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        return false
    }

    private fun computeNormal(p: Point3D): Normal {
        val paramSquared = a * a + b * b
        val sumSquared = p.x * p.x + p.y * p.y + p.z * p.z
        val diff = sumSquared - paramSquared
        val x = 4.0 * p.x * diff
        val y = 4.0 * p.y * (diff + 2.0 * a * a)
        val z = 4.0 * p.z * diff
        return Normal(x, y, z).normalize()
    }

    /*
    private Normal computeNormal(Point3D p) {
        final double paramSquared = a * a + b * b;
        final double sumSquared = p.x * p.x + p.y * p.y + p.z * p.z;
        final double x = 4.0 * p.x * (sumSquared - paramSquared);
        final double y = 4.0 * p.y * (sumSquared - paramSquared + 2.0 * a * a);
        final double z = 4.0 * p.z * (sumSquared - paramSquared);
        final Normal normal = new Normal(x, y, z).normalize();
        return normal;
    }
*/

    override fun equals(other: Any?): Boolean = this.equals<Torus>(other) { a, b ->
        a.a == b.a && a.b == b.b
    }

    override fun hashCode(): Int = this.hash(a, b)

    override fun toString(): String = "Torus($a,$b)"
}
