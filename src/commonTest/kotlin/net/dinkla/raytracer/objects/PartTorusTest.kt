package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class PartTorusTest : StringSpec({
    // Torus sweep radius 2, tube radius 0.5, restricted to azimuth phi in [0, PI] (phi = atan2(x, z)).
    val torus = PartTorus(a = 2.0, b = 0.5, phiMin = 0.0, phiMax = PI)

    "part torus hit on the kept wedge picks the nearest positive root" {
        // From the origin toward +x: inner tube wall at x = 1.5; phi = atan2(1.5, 0) = PI/2, kept.
        val ray = Ray(Point3D(0.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        torus.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.5
        sr.normal.x shouldBeLessThan 0.0 // inner wall normal points back toward -x
    }

    "part torus rejects a ray hitting only the cut-away azimuth" {
        // Toward -x: all hit points have x < 0 (phi outside [0, PI]); a full torus would hit.
        val ray = Ray(Point3D(0.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))

        torus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        torus.shadowHit(ray) shouldBe Shadow.None
    }

    "part torus misses when the ray misses the bounding box" {
        val ray = Ray(Point3D(0.0, 2.0, 0.0), Vector3D(0.0, 1.0, 0.0))

        torus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "part torus shadowHit mirrors hit on the kept wedge" {
        val ray = Ray(Point3D(0.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))

        val shadow = torus.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 1.5
    }

    "part torus does not report a phantom hit for a near-axis ray through the central hole" {
        // Regression for the shared quartic phantom-root bug (TASK-29): a thin part-torus probed by a
        // vertical ray down the central axis must MISS — the solver's spurious roots are surface-rejected.
        val thin = PartTorus(a = 0.9, b = 0.1, phiMin = 0.0, phiMax = PI)
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        thin.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        thin.shadowHit(ray) shouldBe Shadow.None
    }

    "part torus hits a near-axis vertical ray that genuinely pierces the tube in the kept wedge" {
        // x = 0.9 (tube centre), phi = atan2(0.9, 0) = PI/2, kept; the ray crosses the tube top at y = 0.1.
        val thin = PartTorus(a = 0.9, b = 0.1, phiMin = 0.0, phiMax = PI)
        val ray = Ray(Point3D(0.9, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        thin.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.9
    }

    "part torus bounding box contains the full torus" {
        val bbox = torus.boundingBox

        bbox.p shouldBe Point3D(-2.5, -0.5, -2.5)
        bbox.q shouldBe Point3D(2.5, 0.5, 2.5)
    }
})
