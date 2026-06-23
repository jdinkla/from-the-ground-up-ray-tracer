package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Normal.Companion.DOWN
import net.dinkla.raytracer.math.Normal.Companion.RIGHT
import net.dinkla.raytracer.math.Normal.Companion.UP
import net.dinkla.raytracer.math.Normal.Companion.ZERO
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

@Suppress("EqualsNullCall")
class PlaneTest :
    StringSpec({

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

        "shadow hit" {
            // Given
            val plane = Plane(Point3D.ORIGIN, UP)
            val ray = Ray(Point3D(-1.0, -1.0, -1.0), Vector3D.UP)

            // When
            val shadow = plane.shadowHit(ray)

            // Then
            shadow.shouldBeInstanceOf<Shadow.Hit>()
            shadow.t shouldBe 1.0
        }

        "shadow hit returns none when the plane is behind the ray" {
            val plane = Plane(Point3D.ORIGIN, UP)
            val ray = Ray(Point3D(-1.0, 1.0, -1.0), Vector3D.UP)

            plane.shadowHit(ray) shouldBe Shadow.None
        }

        "planes with equal point and normal are equal and share a hashCode" {
            val a = Plane(Point3D.ORIGIN, UP)
            val b = Plane(Point3D.ORIGIN, UP)

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }

        "planes differing in one field are not equal" {
            val base = Plane(Point3D.ORIGIN, UP)

            base shouldNotBe Plane(Point3D(1.0, 0.0, 0.0), UP)
            base shouldNotBe Plane(Point3D.ORIGIN, DOWN)
        }

        "plane is not equal to null or to an unrelated type" {
            val base = Plane(Point3D.ORIGIN, UP)

            base.equals(null) shouldBe false
            base.equals("plane") shouldBe false
        }
    })
