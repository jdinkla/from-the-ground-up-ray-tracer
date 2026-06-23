package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
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

    "smooth triangle interpolates between three distinct vertex normals" {
        // Distinct per-vertex normals so the barycentric blend is non-trivial. At (0.25, 0.25) the
        // weights are (1 - beta - gamma, beta, gamma) = (0.5, 0.25, 0.25), so n0 dominates.
        tri.n0 = Normal(1.0, 0.0, 0.0)
        tri.n1 = Normal(0.0, 1.0, 0.0)
        tri.n2 = Normal(0.0, 0.0, 1.0)

        val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        tri.hit(ray, sr) shouldBe true
        // Blend (0.5, 0.25, 0.25) normalized: x is the largest component (n0 weight 0.5 dominates).
        sr.normal.x shouldBeGreaterThan sr.normal.y
        sr.normal.x shouldBeGreaterThan sr.normal.z
        sr.normal.y shouldBe (sr.normal.z plusOrMinus 1e-6) // symmetric weights on n1 and n2
    }

    "shadowHit mirrors smooth triangle hit branches" {
        val rayMiss = Ray(Point3D(-0.1, 0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(rayMiss) shouldBe Shadow.None

        val rayHit = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))
        val shadow = tri.shadowHit(rayHit)
        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBe (1.0 plusOrMinus 1e-3)
    }

    "smooth triangle shadowHit rejects gamma negative" {
        val ray = Ray(Point3D(0.1, -0.1, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(ray) shouldBe Shadow.None
    }

    "smooth triangle shadowHit rejects beta plus gamma greater than one" {
        val ray = Ray(Point3D(0.8, 0.8, -1.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(ray) shouldBe Shadow.None
    }

    "smooth triangle shadowHit rejects hits below epsilon" {
        val ray = Ray(Point3D(0.1, 0.1, 0.0), Vector3D(0.0, 0.0, 1.0))
        tri.shadowHit(ray) shouldBe Shadow.None
    }

    "equal smooth triangles are equal and share a hash code" {
        val t1 = SmoothTriangle(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0))
        val t2 = SmoothTriangle(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0))

        t1 shouldBe t2
        t1.hashCode() shouldBe t2.hashCode()
    }

    "smooth triangles differing in a vertex are not equal" {
        SmoothTriangle(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0)) shouldNotBe
            SmoothTriangle(Point3D(0.0, 0.0, 0.0), Point3D(2.0, 0.0, 0.0), Point3D(0.0, 1.0, 0.0))
    }

    "toString names the class" {
        tri.toString() shouldContain "SmoothTriangle"
    }
})
