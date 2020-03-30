package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlaneTest {
    private val plane = Plane(Point3D.ORIGIN, Normal.UP)

    @Test
    fun `q=0 plane, point below, vector up, hit`() {
        val o = Point3D(-1.0, -1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        assert(isHit)
        assertEquals(sr.t, 1.0)
        assertEquals(Normal.UP, sr.normal)
    }

    @Test
    fun `q=0 plane, point above, vector up, no hit`() {
        // point above, vector up
        val o = Point3D(-1.0, 1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        assert(!isHit)
        assertEquals(Normal.ZERO, sr.normal) // TODO null and normals
    }

    @Test
    fun `q=0 plane upside down, point below, vector up, hit`() {
        val plane = Plane(Point3D.ORIGIN, Normal.DOWN)

        val o = Point3D(-1.0, -1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        assert(isHit)
        assertEquals(sr.t, 1.0)
        assertEquals(Normal.DOWN, sr.normal)
    }

    @Test
    fun `q=0 plane, point above, vector down, hit`() {
        val o = Point3D(1.0, 2.0, 1.0)
        val d = Vector3D(0.0, -1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        assert(isHit)
        assertEquals(sr.t, 2.0)
        assertEquals(Normal.UP, sr.normal)
    }

    @Test
    fun `plane to the right`() {
        val plane = Plane(Point3D.ORIGIN, Normal.RIGHT)

        val o = Point3D(-2.0, -2.0, 0.0)
        val d = Vector3D(1.0, 1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        assert(isHit)
        assertEquals(sr.t, 2.0)
        assertEquals(Normal.RIGHT, sr.normal)
    }

    @Test
    fun `plane slightly next to origin`() {
        val plane = Plane(Point3D(0.1234, 0.0, 0.0), Normal.RIGHT)

        val o = Point3D(0.0, 4.0, 3.0)
        val d = Vector3D(-1.0, 0.0, 0.0)
        val ray = Ray(o, d)

        val isHit = plane.hit(ray, Hit())
        assert(!isHit)
    }
}
