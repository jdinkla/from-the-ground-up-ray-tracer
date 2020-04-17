package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.abs

internal class SphereTest {

    private val sphere = Sphere(Point3D.ORIGIN, 1.0)

    @Test
    fun boundingBox() {
        val bbox = sphere.boundingBox
        assertEquals(Point3D(-1.0, -1.0, -1.0),  bbox.p)
        assertEquals(Point3D(1.0, 1.0, 1.0),  bbox.q)
    }

    @Test
    fun hit() {
        val sr = Shade()
        val o = Point3D(0.0, 0.0, -2.0)
        val d = Vector3D(0.0, 0.0, 1.0)
        val ray = Ray(o, d)
        val isHit = sphere.hit(ray, sr);
        assert(isHit)
        assert(abs(sr.t - 1.0) < MathUtils.K_EPSILON)
        assertEquals(Normal.BACKWARD, sr.normal)
    }

    // TODO test for not hit

    @Test
    fun shadowHit() {
        // TODO test for shadowHit
    }
}