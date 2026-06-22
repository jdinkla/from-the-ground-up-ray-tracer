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

    // Regression for the phantom-intersection bug: a thin torus probed by a near-axis vertical ray.
    // The quartic solver returns spurious roots whose hit points are NOT on the torus surface; without
    // validation, hit() wrongly reported an intersection inside the central hole.
    "torus does not report a phantom hit for a ray going down the central hole" {
        // Torus(0.9, 0.1): tube swept at radius 0.9, y in [-0.1, 0.1]. A ray straight down the y-axis
        // (x = z = 0) passes through the empty central hole and must MISS.
        val torus = Torus(a = 0.9, b = 0.1)
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        torus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "torus hits a near-axis vertical ray that genuinely pierces the tube" {
        // Torus(0.9, 0.1): a vertical ray at x = 0.9 (the tube's centre circle) drops through the tube
        // core, first crossing the top of the tube at y = 0.1, i.e. t = 3 - 0.1 = 2.9.
        val torus = Torus(a = 0.9, b = 0.1)
        val ray = Ray(Point3D(0.9, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        torus.hit(ray, sr) shouldBe true
        sr.t shouldBe (2.9 plusOrMinus 1e-3)
        sr.normal.y shouldBeGreaterThan 0.0 // top of the tube faces +y
    }

    "torus misses a vertical ray outside the outer radius" {
        // Torus(0.9, 0.1): outer radius is a + b = 1.0; a vertical ray at x = 1.2 clears the tube.
        val torus = Torus(a = 0.9, b = 0.1)
        val ray = Ray(Point3D(1.2, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        torus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }
})
