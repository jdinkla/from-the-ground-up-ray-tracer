package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class TorusTest : StringSpec({
    "torus rejects rays that miss the bounding box" {
        val torus = Torus(a = 2.0, b = 0.5)
        val ray = Ray(Point3D(0.0, 2.0, 0.0), Vector3D(0.0, 1.0, 0.0))
        torus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "torus returns false when degenerate ray yields no roots" {
        val torus = Torus(a = 2.0, b = 0.5)
        val ray = Ray(Point3D(0.0, 0.0, 0.0), Vector3D(0.0, 0.0, 0.0))
        torus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "torus returns false when all roots are behind the origin" {
        val torus = Torus(a = 2.0, b = 0.5)
        val ray = Ray(Point3D(3.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        torus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "torus picks the nearest positive root and sets normal" {
        val torus = Torus(a = 2.0, b = 0.5)
        val ray = Ray(Point3D(0.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        torus.hit(ray, sr) shouldBe true
        sr.t shouldBe (1.5 plusOrMinus 1e-3)
        sr.normal.x shouldBeLessThan(0.0)
        sr.normal.x.shouldBeGreaterThan(-1.1) // normalized toward -X
    }
})
