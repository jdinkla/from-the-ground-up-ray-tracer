package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal.Companion.DOWN
import net.dinkla.raytracer.math.Normal.Companion.RIGHT
import net.dinkla.raytracer.math.Normal.Companion.UP
import net.dinkla.raytracer.math.Normal.Companion.ZERO
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class PlaneTest : StringSpec({

    "q=0 plane, point below, vector up, hit" {
        val plane = Plane(Point3D.ORIGIN, UP)
        val o = Point3D(-1.0, -1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        isHit shouldBe true
        sr.t shouldBe 1.0
        sr.normal shouldBe UP
    }

    "q=0 plane, point above, vector up, no hit" {
        val plane = Plane(Point3D.ORIGIN, UP)
        val o = Point3D(-1.0, 1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        isHit shouldBe false
        sr.normal shouldBe ZERO
    }

    "q=0 plane upside down, point below, vector up, hit" {
        val plane = Plane(Point3D.ORIGIN, DOWN)
        val o = Point3D(-1.0, -1.0, -1.0)
        val d = Vector3D(0.0, 1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        isHit shouldBe true
        sr.t shouldBe 1.0
        sr.normal shouldBe DOWN
    }

    "q=0 plane, point above, vector down, hit" {
        val plane = Plane(Point3D.ORIGIN, UP)
        val o = Point3D(1.0, 2.0, 1.0)
        val d = Vector3D(0.0, -1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        isHit shouldBe true
        sr.t shouldBe 2.0
        sr.normal shouldBe UP
    }

    "plane to the right" {
        val plane = Plane(Point3D.ORIGIN, RIGHT)
        val o = Point3D(-2.0, -2.0, 0.0)
        val d = Vector3D(1.0, 1.0, 0.0)
        val ray = Ray(o, d)
        val sr = Hit()

        val isHit = plane.hit(ray, sr)
        isHit shouldBe true
        sr.t shouldBe 2.0
        sr.normal shouldBe RIGHT
    }

    "plane slightly next to origin" {
        val plane = Plane(Point3D(0.1234, 0.0, 0.0), RIGHT)
        val o = Point3D(0.0, 4.0, 3.0)
        val d = Vector3D(-1.0, 0.0, 0.0)
        val ray = Ray(o, d)

        val isHit = plane.hit(ray, Hit())
        isHit shouldBe false
    }
})

