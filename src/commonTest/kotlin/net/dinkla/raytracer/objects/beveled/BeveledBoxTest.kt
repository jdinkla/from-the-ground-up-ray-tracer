package net.dinkla.raytracer.objects.beveled

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
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
class BeveledBoxTest : StringSpec({
    // A beveled box spanning (-1,-1,-1)..(1,1,1) with edge bevel radius 0.2. The flat top face lives at
    // y = 1 and spans x,z in [-0.8, 0.8] (the bevel narrows each face); rounded cylinder/sphere edges
    // round the box's edges and corners.
    val p0 = Point3D(-1.0, -1.0, -1.0)
    val p1 = Point3D(1.0, 1.0, 1.0)
    val rb = 0.2

    fun box() = BeveledBox(p0, p1, rb).apply { initialize() }

    "beveled box hit straight down onto the flat top face records t and the UP normal" {
        // From (0, 3, 0) toward -y: the top face at y = 1 is reached at (0, 1, 0), t = 2. The point sits
        // inside the narrowed face band [-0.8, 0.8], away from the rounded edges.
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        box().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.UP
    }

    "beveled box hit on the flat right face records the +x normal" {
        // From (3, 0, 0) toward -x: the right face at x = 1 is reached at (1, 0, 0), t = 2.
        val ray = Ray(Point3D(3.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        box().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.RIGHT
    }

    "beveled box misses a ray aimed away from it" {
        // From (3, 0, 0) toward +x travels away from the box.
        val ray = Ray(Point3D(3.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))

        box().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "beveled box shadowHit reports a shadow for a ray that strikes the top face" {
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        box().shadowHit(ray).shouldBeInstanceOf<Shadow.Hit>()
    }

    "beveled box bounding box encloses the full box extent" {
        val bbox = box().boundingBox

        bbox.p.x shouldBeLessThanOrEqual -1.0
        bbox.p.y shouldBeLessThanOrEqual -1.0
        bbox.p.z shouldBeLessThanOrEqual -1.0
        bbox.q.x shouldBeGreaterThan 1.0 - 1e-9
        bbox.q.y shouldBeGreaterThan 1.0 - 1e-9
        bbox.q.z shouldBeGreaterThan 1.0 - 1e-9
    }

    "wireframe beveled box omits the flat faces so a centred top ray misses" {
        // A wireframe box has no face rectangles, only the rounded edges/corners; a ray down the centre
        // (x = z = 0) passes between the edges and hits nothing.
        val wire = BeveledBox(p0, p1, rb, isWiredFrame = true).apply { initialize() }
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        wire.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "beveled boxes with equal fields are equal and share a hashCode" {
        val a = BeveledBox(p0, p1, rb)
        val b = BeveledBox(p0, p1, rb)

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "beveled boxes differing in one field are not equal" {
        val base = BeveledBox(p0, p1, rb)

        base shouldNotBe BeveledBox(Point3D(-2.0, -1.0, -1.0), p1, rb)
        base shouldNotBe BeveledBox(p0, Point3D(2.0, 1.0, 1.0), rb)
        base shouldNotBe BeveledBox(p0, p1, 0.3)
        base shouldNotBe BeveledBox(p0, p1, rb, isWiredFrame = true)
    }

    "beveled box is not equal to null or to an unrelated type" {
        val base = BeveledBox(p0, p1, rb)

        base.equals(null) shouldBe false
        base.equals("box") shouldBe false
    }

    "beveled box toString names the class" {
        BeveledBox(p0, p1, rb).toString() shouldContain "BeveledBox"
    }
})
