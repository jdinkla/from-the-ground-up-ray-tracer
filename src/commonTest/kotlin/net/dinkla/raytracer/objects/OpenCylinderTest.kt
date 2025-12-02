package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class OpenCylinderTest : StringSpec({
    val cylinder = OpenCylinder(-1.0, 1.0, 1.0)

    "open cylinder rejects misses (disc < 0)" {
        val ray = Ray(Point3D(2.5, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        cylinder.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        cylinder.shadowHit(ray) shouldBe Shadow.None
    }

    "open cylinder rejects hits outside y range" {
        val ray = Ray(Point3D(0.0, 2.0, -2.0), Vector3D(0.0, 0.0, 1.0))
        cylinder.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        cylinder.shadowHit(ray) shouldBe Shadow.None
    }

    "open cylinder returns nearest valid intersection" {
        val ray = Ray(Point3D(0.0, 0.0, -2.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder.hit(ray, sr) shouldBe true
        sr.t shouldBe (1.0 plusOrMinus 1e-3)
        sr.normal.y shouldBe 0.0
        sr.normal.x shouldBe 0.0 // along +Z; normal should have zero x
    }

    "open cylinder flips normal when hit from inside" {
        val ray = Ray(Point3D(0.5, 0.0, 0.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder.hit(ray, sr) shouldBe true
        sr.normal.z.shouldBeLessThan(0.0) // flipped to point outward relative to incoming ray
    }

    "shadowHit mirrors hit behavior" {
        val ray = Ray(Point3D(0.0, 0.0, -2.0), Vector3D(0.0, 0.0, 1.0))
        val shadow = cylinder.shadowHit(ray)
        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBe (1.0 plusOrMinus 1e-3)
    }
})
