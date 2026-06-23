package net.dinkla.raytracer.objects.compound

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

@Suppress("EqualsNullCall")
class BoxTest : StringSpec({
    // An axis-aligned box spanning (0,0,0)..(2,2,2): p0 at the origin with edge vectors along +x/+y/+z.
    val p0 = Point3D.ORIGIN
    val edgeA = Vector3D(2.0, 0.0, 0.0)
    val edgeB = Vector3D(0.0, 2.0, 0.0)
    val edgeC = Vector3D(0.0, 0.0, 2.0)

    fun box() = Box(p0, edgeA, edgeB, edgeC).apply { initialize() }

    "box hit on the near z = 0 face records the nearer t" {
        // From (1, 1, -5) toward +z the ray pierces the centre; the z = 0 face is reached at (1, 1, 0),
        // t = 5, nearer than the far z = 2 face at t = 7.
        val ray = Ray(Point3D(1.0, 1.0, -5.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        box().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 5.0
    }

    "box hit on the x = 0 face from the left records the nearer t" {
        // From (-3, 1, 1) toward +x: the x = 0 face is reached at (0, 1, 1), t = 3.
        val ray = Ray(Point3D(-3.0, 1.0, 1.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        box().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 3.0
    }

    "box misses a ray that passes outside every face" {
        // From (5, 5, -5) toward +z: stays at x = y = 5, outside the [0,2] box.
        val ray = Ray(Point3D(5.0, 5.0, -5.0), Vector3D(0.0, 0.0, 1.0))

        box().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "box shadowHit reports a shadow for a ray that strikes a face" {
        val ray = Ray(Point3D(1.0, 1.0, -5.0), Vector3D(0.0, 0.0, 1.0))

        box().shadowHit(ray).shouldBeInstanceOf<Shadow.Hit>()
    }

    "box bounding box encloses p0 and the far corner" {
        val bbox = box().boundingBox

        // Compound pads the computed bounds slightly, so assert enclosure rather than the exact corners.
        bbox.p.x shouldBeLessThanOrEqual 0.0
        bbox.p.y shouldBeLessThanOrEqual 0.0
        bbox.p.z shouldBeLessThanOrEqual 0.0
        bbox.q.x shouldBeGreaterThanOrEqual 2.0
        bbox.q.y shouldBeGreaterThanOrEqual 2.0
        bbox.q.z shouldBeGreaterThanOrEqual 2.0
    }

    "boxes with equal fields are equal and share a hashCode" {
        val a = Box(p0, edgeA, edgeB, edgeC)
        val b = Box(p0, edgeA, edgeB, edgeC)

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "boxes differing in one field are not equal" {
        val base = Box(p0, edgeA, edgeB, edgeC)

        base shouldNotBe Box(Point3D(1.0, 0.0, 0.0), edgeA, edgeB, edgeC)
        base shouldNotBe Box(p0, Vector3D(3.0, 0.0, 0.0), edgeB, edgeC)
        base shouldNotBe Box(p0, edgeA, Vector3D(0.0, 3.0, 0.0), edgeC)
        base shouldNotBe Box(p0, edgeA, edgeB, Vector3D(0.0, 0.0, 3.0))
    }

    "box is not equal to null or to an unrelated type" {
        val base = Box(p0, edgeA, edgeB, edgeC)

        base.equals(null) shouldBe false
        base.equals("box") shouldBe false
    }

    "box toString names the class" {
        Box(p0, edgeA, edgeB, edgeC).toString() shouldContain "Box"
    }
})
