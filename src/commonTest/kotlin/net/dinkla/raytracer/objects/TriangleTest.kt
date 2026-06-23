package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class TriangleTest : StringSpec({
    val tri = Triangle(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0))

    "triangle rejects beta negative" {
        val ray = Ray(Point3D(-0.1, 0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "triangle rejects gamma negative" {
        val ray = Ray(Point3D(0.1, -0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "triangle rejects beta plus gamma greater than one" {
        val ray = Ray(Point3D(0.8, 0.8, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "triangle rejects hits below epsilon" {
        val ray = Ray(Point3D(0.1, 0.1, 0.0), Vector3D(0.0, 0.0, 1.0))
        tri.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "triangle accepts valid hit and sets normal" {
        val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        tri.hit(ray, sr) shouldBe true
        sr.t shouldBe (1.0 plusOrMinus 1e-3)
        sr.normal shouldBe tri.normal
    }

    "triangle shadowHit mirrors hit branches" {
        val rayMiss = Ray(Point3D(-0.1, 0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(rayMiss) shouldBe Shadow.None

        val rayHit = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))
        val shadow = tri.shadowHit(rayHit)
        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBe (1.0 plusOrMinus 1e-3)
    }

    "triangle shadowHit rejects gamma negative" {
        val ray = Ray(Point3D(0.1, -0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(ray) shouldBe Shadow.None
    }

    "triangle shadowHit rejects beta plus gamma greater than one" {
        val ray = Ray(Point3D(0.8, 0.8, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(ray) shouldBe Shadow.None
    }

    "triangle shadowHit rejects hits below epsilon" {
        val ray = Ray(Point3D(0.1, 0.1, 0.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(ray) shouldBe Shadow.None
    }

    "equal triangles are equal and share a hash code" {
        val t1 = Triangle(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0))
        val t2 = Triangle(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0))

        t1 shouldBe t2
        t1.hashCode() shouldBe t2.hashCode()
    }

    "triangles differing in a vertex are not equal" {
        tri shouldNotBe Triangle(Point3D(0.0, 0.0, 0.0), Point3D(2.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0))
    }

    "toString names the class" {
        tri.toString() shouldContain "Triangle"
    }
})
