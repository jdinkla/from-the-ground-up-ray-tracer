package net.dinkla.raytracer.objects.beveled

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class BeveledWedgeTest : StringSpec({
    // A wedge of a thick tube: y in [0, 2], inner radius 1, outer radius 2, azimuth phi in [0, PI/2]
    // (phi = atan2(x, z): phi = 0 is the +z face, phi = PI/2 is the +x face), edge bevel 0.2.
    fun wedge() =
        BeveledWedge(
            y0 = 0.0,
            y1 = 2.0,
            innerRadius = 1.0,
            outerRadius = 2.0,
            phiMin = 0.0,
            phiMax = PI / 2.0,
            rb = 0.2,
        ).apply { initialize() }

    "beveled wedge hit on the OUTER wall records t and an outward +x normal" {
        // From (5, 1, 0.001) toward -x (phi just under PI/2, inside the wedge): the outer cylinder at
        // radius 2 is reached at x = 2, t = 3.
        val ray = Ray(Point3D(5.0, 1.0, 0.001), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        wedge().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 3.0
        sr.normal.x shouldBeGreaterThan 0.0
    }

    "beveled wedge hit from the hollow strikes the INNER wall with an inward-facing normal" {
        // From (0, 1, 0.001) toward +x: the inner cylinder (radius 1) is reached at x = 1, t = 1.
        // The inner wall faces the incoming ray, so its normal points back toward -x.
        val ray = Ray(Point3D(0.0, 1.0, 0.001), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        wedge().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal.x shouldBeLessThan 0.0
    }

    "beveled wedge hit straight down onto the top CAP records t and the UP normal" {
        // From (1.0607, 3, 1.0607) toward -y (phi = PI/4, radius 1.5, inside the narrowed cap band):
        // the top part-annulus at y = 2 is reached at t = 1.
        val ray = Ray(Point3D(1.0607, 3.0, 1.0607), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        wedge().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBe Normal.UP
    }

    "beveled wedge hit on the rounded outer top RIM lands on the part-torus apex" {
        // From (1.2728, 3, 1.2728) toward -y (phi = PI/4, x = z = (outerRadius - rb)/sqrt2 = 1.8/sqrt2):
        // the outer rim's tube top is at y = (y1 - rb) + rb = 2.0, t = 1. Relies on the torus fix.
        val ray = Ray(Point3D(1.2728, 3.0, 1.2728), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        wedge().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal.y shouldBeGreaterThan 0.0 // the rim apex faces +y
    }

    "beveled wedge hit on a flat RADIAL side records the side normal" {
        // The phi = 0 radial face lies in the plane z = const containing the y-axis and +z; its normal is
        // along -x. From (-0.5, 1, 1.5) toward +x the ray meets that face at (0, 1, 1.5), t = 0.5.
        val ray = Ray(Point3D(-0.5, 1.0, 1.5), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        wedge().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 0.5
        sr.normal.x shouldBeLessThan 0.0
    }

    "beveled wedge misses a ray dropping outside the kept azimuth wedge" {
        // From (-1.5, 3, -1.5) straight down: phi = atan2(-1.5, -1.5) = 5PI/4, well outside [0, PI/2].
        val ray = Ray(Point3D(-1.5, 3.0, -1.5), Vector3D(0.0, -1.0, 0.0))

        wedge().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "beveled wedge shadowHit reports a shadow for a ray that strikes the outer wall" {
        val ray = Ray(Point3D(5.0, 1.0, 0.001), Vector3D(-1.0, 0.0, 0.0))

        val shadow = wedge().shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
    }

    "beveled wedge bounding box contains the full outer radius and height" {
        val bbox = wedge().boundingBox

        // Conservative enclosure of the radius-2, y in [0,2] sector (Instance rims may overestimate).
        bbox.p.x shouldBeLessThanOrEqual -2.0
        bbox.p.y shouldBeLessThanOrEqual 0.0
        bbox.q.x shouldBeGreaterThan 2.0 - 1e-9
        bbox.q.y shouldBeGreaterThan 2.0 - 1e-9
    }
})
