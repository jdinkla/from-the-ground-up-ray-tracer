package net.dinkla.raytracer.objects.compound

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

@Suppress("EqualsNullCall")
class FishBowlTest : StringSpec({
    // Three distinct stand-in materials so per-boundary assignment is observable by identity.
    val glassAir: IMaterial = Matte(Color(0.1, 0.1, 0.1))
    val waterAir: IMaterial = Matte(Color(0.2, 0.2, 0.2))
    val waterGlass: IMaterial = Matte(Color(0.3, 0.3, 0.3))

    fun bowl(): FishBowl =
        FishBowl(
            glassAir = glassAir,
            waterAir = waterAir,
            waterGlass = waterGlass,
        )

    "a fishbowl is assembled from five boundary surfaces" {
        // glass-air: outer sphere, inner sphere above water, rim torus (3);
        // water-glass: inner sphere below water (1);
        // water-air: water surface disk (1).
        bowl().objects.size shouldBe 5
    }

    "every boundary surface carries one of the three dielectric materials, never a propagated single one" {
        val materials = bowl().objects.map { it.material }.toSet()

        materials shouldBe setOf(glassAir, waterAir, waterGlass)
    }

    "the glass-air boundary owns exactly three surfaces (outer sphere, inner upper sphere, rim)" {
        bowl().objects.count { it.material === glassAir } shouldBe 3
    }

    "the water-glass boundary owns exactly one surface (the submerged inner wall)" {
        bowl().objects.count { it.material === waterGlass } shouldBe 1
    }

    "the water-air boundary owns exactly one surface (the flat water surface disk)" {
        bowl().objects.count { it.material === waterAir } shouldBe 1
    }

    "a horizontal ray strikes the convex outer glass at the outer radius with an outward normal" {
        // From (5, 0, 0) toward -x: the outer sphere (radius 2.0) is reached at (2, 0, 0), t = 3.
        // y = 0 is theta = pi/2, inside the kept band [thetaOpening, pi], so the equator is glass.
        val ray = Ray(Point3D(5.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        val bowl = bowl()
        bowl.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 3.0
        sr.normal shouldBeApprox Normal.RIGHT
        sr.geometricObject?.material shouldBe glassAir
    }

    "a ray dropped down the axis meets the flat water surface, not the glass (the top is open)" {
        // From (0, 5, 0) toward -y: the opening removes theta near 0 from every sphere, so the axis is
        // clear of glass at the top; the first hit is the water-surface disk at y = 0.8 (t = 4.2), up.
        val ray = Ray(Point3D(0.0, 5.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        val bowl = bowl()
        bowl.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 4.2
        sr.normal shouldBeApprox Normal.UP
        sr.geometricObject?.material shouldBe waterAir
    }

    "a ray rising from below strikes the outer glass bottom with a downward normal" {
        // From (0, -5, 0) toward +y: the outer sphere bottom (0, -2, 0) is theta = pi (kept), reached at
        // t = 3, outward normal down; it is a glass-air boundary.
        val ray = Ray(Point3D(0.0, -5.0, 0.0), Vector3D(0.0, 1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        val bowl = bowl()
        bowl.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 3.0
        sr.normal shouldBeApprox Normal.DOWN
        sr.geometricObject?.material shouldBe glassAir
    }

    "the bounding box spans the outer radius of the glass sphere in every direction" {
        val bbox = bowl().boundingBox

        bbox.p.x shouldBeApprox -2.0
        bbox.p.y shouldBeApprox -2.0
        bbox.q.x shouldBeApprox 2.0
        bbox.q.y shouldBeApprox 2.0
    }

    "a ray passing wide of the bounding box misses the fishbowl" {
        val ray = Ray(Point3D(8.0, 0.0, 0.0), Vector3D(0.0, 0.0, 1.0))

        bowl().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "fishbowls with equal fields are equal and share a hashCode" {
        val a = bowl()
        val b = bowl()

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "fishbowls differing in one geometric field are not equal" {
        val base = bowl()

        base shouldNotBe
            FishBowl(glassAir, waterAir, waterGlass, waterY = 0.5)
        base shouldNotBe
            FishBowl(glassAir, waterAir, waterGlass, innerRadius = 1.7)
    }

    "a fishbowl is not equal to null or to an unrelated type" {
        val base = bowl()

        base.equals(null) shouldBe false
        base.equals("bowl") shouldBe false
    }

    "fishbowl toString names the class and key dimensions" {
        bowl().toString() shouldContain "FishBowl"
    }
})
