package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
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
})
