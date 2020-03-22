package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class PlaneTest {

    // Plane
    private val p = Point3D(0.0, 0.0, 0.0)
    private var normal = Normal(0.0, 1.0, 0.0)
    private var plane = Plane(p, normal)

    // Sample
    private var sr: Hit = Hit()

    @BeforeEach
    fun init() {
        sr = Hit()
    }

    @Test
    fun construct() {
        Plane(Point3D.ORIGIN, Normal.DOWN)
    }

    // q=0 plane, point below, vector up, hit
    @Test
    fun hit0() {
        val o = Point3D(-1.0, -1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)

        val isHit = plane.hit(ray, sr)
        assert(isHit)
        assertEquals(sr.t, 1.0)
        assertEquals(sr.normal, normal)
    }

    // q=0 plane, point above, vector up, no hit
    @Test
    fun hit1() {
        // point above, vector up
        val o = Point3D(-1.0, 1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)

        val isHit = plane.hit(ray, sr)
        assert(!isHit)
        assertEquals(Normal.ZERO, sr.normal) // TODO null and normals
    }

    // q=0 plane upside down, point below, vector up, hit
    @Test
    fun hit2() {
        val normal = Normal(0.0, -1.0, 0.0)
        val plane = Plane(p, normal)

        val o = Point3D(-1.0, -1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)

        val isHit = plane.hit(ray, sr)
        assert(isHit)
        assertEquals(sr.t, 1.0)
        assertEquals(sr.normal, normal)
    }

    // q=0 plane, point above, vector down, hit
    @Test
    fun hit3() {
        val o = Point3D(1.0, 2.0, 1.0)
        val d = Vector3D(0.0, -1.0, 0.0)
        val ray = Ray(o, d)

        val isHit = plane.hit(ray, sr)
        assert(isHit)
        assertEquals(sr.t, 2.0)
        assertEquals(sr.normal, normal)
    }

    @Test
    fun hit4() {
        val p = Point3D(0.0, 0.0, 0.0)
        val normal = Normal(1.0, 1.0, 0.0)
        val plane = Plane(p, normal)

        val o = Point3D(-2.0, -2.0, 0.0)
        val d = Vector3D(1.0, 1.0, 0.0)
        val ray = Ray(o, d)

        val isHit = plane.hit(ray, sr)
        assert(isHit)
        assertEquals(sr.t, 2.0)
        assertEquals(sr.normal, normal)
    }

    @Test
    fun hit5() {
        val p = Point3D(0.1234, 0.0, 0.0)
        val normal = Normal(1.0, 0.0, 0.0)
        val plane = Plane(p, normal)

        val o = Point3D(0.0, 4.0, 3.0)
        val d = Vector3D(-1.0, 0.0, 0.0)
        val ray = Ray(o, d)

        val isHit = plane.hit(ray, sr)
        assert(!isHit)
    }

}
