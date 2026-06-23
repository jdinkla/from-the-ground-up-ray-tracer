package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
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
import net.dinkla.raytracer.shouldBeApprox

@Suppress("EqualsNullCall")
class AnnulusTest : StringSpec({
    // Ring in the y = 0 plane, hole radius 0.5, outer radius 1.0, facing +y.
    val annulus = Annulus(Point3D.ORIGIN, 0.5, 1.0, Normal.UP)

    "annulus hit in the band records t and the plane normal" {
        // Straight down onto the band at radius 0.75 (between 0.5 and 1.0).
        val ray = Ray(Point3D(0.75, 2.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        annulus.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.UP
    }

    "annulus rejects a ray through the central hole" {
        // Radius 0.2 < inner radius 0.5: falls in the hole.
        val ray = Ray(Point3D(0.2, 2.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        annulus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        annulus.shadowHit(ray) shouldBe Shadow.None
    }

    "annulus rejects a ray outside the outer radius" {
        // Radius 1.5 > outer radius 1.0.
        val ray = Ray(Point3D(1.5, 2.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        annulus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        annulus.shadowHit(ray) shouldBe Shadow.None
    }

    "annulus shadowHit mirrors hit on the band" {
        val ray = Ray(Point3D(0.75, 2.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        val shadow = annulus.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 2.0
    }

    "annulus rejects a ray whose plane intersection is behind the origin" {
        // From (0.75, -2, 0) toward -y: the y = 0 plane is reached at t = -2 (behind), so no hit.
        val ray = Ray(Point3D(0.75, -2.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        annulus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        annulus.shadowHit(ray) shouldBe Shadow.None
    }

    "annulus bounding box contains the outer ring" {
        val bbox = annulus.boundingBox

        bbox.p shouldBe Point3D(-1.0, -1.0, -1.0)
        bbox.q shouldBe Point3D(1.0, 1.0, 1.0)
    }

    "annuli with equal fields are equal and share a hashCode" {
        val a = Annulus(Point3D.ORIGIN, 0.5, 1.0, Normal.UP)
        val b = Annulus(Point3D.ORIGIN, 0.5, 1.0, Normal.UP)

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "annuli differing in one field are not equal" {
        val base = Annulus(Point3D.ORIGIN, 0.5, 1.0, Normal.UP)

        base shouldNotBe Annulus(Point3D(1.0, 0.0, 0.0), 0.5, 1.0, Normal.UP)
        base shouldNotBe Annulus(Point3D.ORIGIN, 0.25, 1.0, Normal.UP)
        base shouldNotBe Annulus(Point3D.ORIGIN, 0.5, 2.0, Normal.UP)
        base shouldNotBe Annulus(Point3D.ORIGIN, 0.5, 1.0, Normal.DOWN)
    }

    "annulus is not equal to null or to an unrelated type" {
        val base = Annulus(Point3D.ORIGIN, 0.5, 1.0, Normal.UP)

        base.equals(null) shouldBe false
        base.equals("annulus") shouldBe false
    }

    "annulus toString contains the class name" {
        Annulus(Point3D.ORIGIN, 0.5, 1.0, Normal.UP).toString() shouldContain "Annulus"
    }
})
