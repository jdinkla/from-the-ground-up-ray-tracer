package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class SmoothTriangleTest : StringSpec({
    val tri = SmoothTriangle(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0))

    "smooth triangle rejects beta negative" {
        val ray = Ray(Point3D(-0.1, 0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "smooth triangle rejects gamma negative" {
        val ray = Ray(Point3D(0.1, -0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "smooth triangle rejects beta plus gamma greater than one" {
        val ray = Ray(Point3D(0.8, 0.8, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "smooth triangle rejects hits below epsilon" {
        val ray = Ray(Point3D(0.1, 0.1, 0.0), Vector3D(0.0, 0.0, 1.0))
        tri.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "smooth triangle accepts valid hit and interpolates normal" {
        tri.n0 = Normal(0.0, 0.0, 1.0)
        tri.n1 = Normal(0.0, 0.0, 1.0)
        tri.n2 = Normal(0.0, 0.0, 1.0)

        val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        tri.hit(ray, sr) shouldBe true
        sr.t shouldBe (1.0 plusOrMinus 1e-3)
        sr.normal shouldBe Normal(0.0, 0.0, 1.0)
    }

    "shadowHit mirrors smooth triangle hit branches" {
        val rayMiss = Ray(Point3D(-0.1, 0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(rayMiss) shouldBe Shadow.None

        val rayHit = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))
        val shadow = tri.shadowHit(rayHit)
        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBe (1.0 plusOrMinus 1e-3)
    }
})
