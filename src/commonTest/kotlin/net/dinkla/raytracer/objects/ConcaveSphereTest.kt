package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

@Suppress("EqualsNullCall")
class ConcaveSphereTest : StringSpec({
    // Unit concave sphere at the origin: same surface as a Sphere, but the normal points inward.
    val sphere = ConcaveSphere(Point3D.ORIGIN, 1.0)

    "concave sphere hit records t and an INWARD normal (pointing toward the centre)" {
        // From (2,0,0) toward -x, the near surface is reached at (1,0,0), t = 1.
        // A plain Sphere would report the outward normal +x; the concave sphere reports -x.
        val ray = Ray(Point3D(2.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        sphere.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.LEFT
    }

    "concave sphere hit from inside reaches the far wall with an inward normal" {
        // From the centre toward +x: first root is behind (t = -1), the wall is reached at (1,0,0), t = 1.
        // The inward normal at (1,0,0) still points back toward the centre, i.e. -x.
        val ray = Ray(Point3D.ORIGIN, Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        sphere.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.LEFT
    }

    "concave sphere misses when the ray misses the sphere entirely" {
        val ray = Ray(Point3D(2.0, 2.0, 0.0), Vector3D(0.0, 1.0, 0.0))

        sphere.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        sphere.shadowHit(ray) shouldBe Shadow.None
    }

    "concave sphere shadowHit reports the nearest forward intersection" {
        val ray = Ray(Point3D(2.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))

        val shadow = sphere.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 1.0
    }

    "concave sphere rejects a ray whose only intersections lie behind the origin" {
        // From (5,0,0) toward +x: the sphere sits behind the origin, both roots are negative.
        val ray = Ray(Point3D(5.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))

        sphere.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        sphere.shadowHit(ray) shouldBe Shadow.None
    }

    "concave sphere bounding box contains the sphere" {
        val bbox = sphere.boundingBox

        bbox.p shouldBe Point3D(-1.0, -1.0, -1.0)
        bbox.q shouldBe Point3D(1.0, 1.0, 1.0)
    }

    "concave sphere built with a material exposes that material" {
        val material = Matte()
        val withMaterial = ConcaveSphere(Point3D.ORIGIN, 1.0, material)

        withMaterial.material shouldBe material
    }

    "concave spheres with equal centre and radius are equal and share a hashCode" {
        val a = ConcaveSphere(Point3D.ORIGIN, 1.0)
        val b = ConcaveSphere(Point3D.ORIGIN, 1.0)

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "concave spheres differing in one field are not equal" {
        val base = ConcaveSphere(Point3D.ORIGIN, 1.0)

        base shouldNotBe ConcaveSphere(Point3D(1.0, 0.0, 0.0), 1.0)
        base shouldNotBe ConcaveSphere(Point3D.ORIGIN, 2.0)
    }

    "concave sphere is not equal to null or to an unrelated type" {
        val base = ConcaveSphere(Point3D.ORIGIN, 1.0)

        base.equals(null) shouldBe false
        base.equals("sphere") shouldBe false
    }

    "concave sphere toString contains the class name" {
        ConcaveSphere(Point3D.ORIGIN, 1.0).toString() shouldContain "ConcaveSphere"
    }
})
