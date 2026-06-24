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
class GlassOfWaterTest : StringSpec({
    // Three distinct stand-in materials so per-boundary assignment is observable by identity.
    val glassAir: IMaterial = Matte(Color(0.1, 0.1, 0.1))
    val waterGlass: IMaterial = Matte(Color(0.2, 0.2, 0.2))
    val waterAir: IMaterial = Matte(Color(0.3, 0.3, 0.3))

    fun glass(): GlassOfWater =
        GlassOfWater(
            glassAir = glassAir,
            waterGlass = waterGlass,
            waterAir = waterAir,
        )

    "glass of water is assembled from eight boundary surfaces" {
        // glass-air: top rim, outer wall, upper inner wall, bottom (4);
        // water-glass: lower inner wall, cavity floor (2);
        // water-air: water surface, meniscus (2).
        glass().objects.size shouldBe 8
    }

    "every boundary surface carries one of the three dielectric materials, never a propagated single one" {
        val materials = glass().objects.map { it.material }.toSet()

        materials shouldBe setOf(glassAir, waterGlass, waterAir)
    }

    "the glass-air boundary owns exactly four surfaces" {
        glass().objects.count { it.material === glassAir } shouldBe 4
    }

    "the water-glass boundary owns exactly two surfaces" {
        glass().objects.count { it.material === waterGlass } shouldBe 2
    }

    "the water-air boundary owns exactly two surfaces (surface disk + meniscus)" {
        glass().objects.count { it.material === waterAir } shouldBe 2
    }

    "a horizontal ray strikes the convex outer wall at the outer radius with an outward normal" {
        // From (3, 1, 0) toward -x: the outer cylinder (radius 1.0) is reached at (1, 1, 0), t = 2.
        val ray = Ray(Point3D(3.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        glass().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.RIGHT
    }

    "a ray dropped down the axis meets the flat water surface, not the glass walls" {
        // From (0, 3, 0) toward -y: the rim annulus and the radius-0.9 inner wall are both missed on
        // the axis, so the first hit is the water-surface disk at y = 1.4 (t = 1.6), normal up.
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        val glass = glass()
        glass.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.6
        sr.normal shouldBeApprox Normal.UP
        sr.geometricObject?.material shouldBe waterAir
    }

    "a ray rising from below strikes the glass bottom with a downward normal" {
        // From (0, -1, 0) toward +y: the bottom disk at y = 0 (radius = outer radius) is reached at
        // (0, 0, 0), t = 1, normal down; it is a glass-air boundary.
        val ray = Ray(Point3D(0.0, -1.0, 0.0), Vector3D(0.0, 1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        val glass = glass()
        glass.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.DOWN
        sr.geometricObject?.material shouldBe glassAir
    }

    "the bounding box spans the outer radius and the full height of the glass" {
        val bbox = glass().boundingBox

        bbox.p.x shouldBeApprox -1.0
        bbox.p.y shouldBeApprox 0.0
        bbox.q.x shouldBeApprox 1.0
        bbox.q.y shouldBeApprox 2.0
    }

    "a ray passing wide of the bounding box misses the glass" {
        val ray = Ray(Point3D(5.0, 1.0, 0.0), Vector3D(0.0, 0.0, 1.0))

        glass().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "glasses with equal fields are equal and share a hashCode" {
        val a = glass()
        val b = glass()

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "glasses differing in one geometric field are not equal" {
        val base = glass()

        base shouldNotBe
            GlassOfWater(glassAir, waterGlass, waterAir, waterY = 1.0)
        base shouldNotBe
            GlassOfWater(glassAir, waterGlass, waterAir, innerRadius = 0.8)
    }

    "glass of water is not equal to null or to an unrelated type" {
        val base = glass()

        base.equals(null) shouldBe false
        base.equals("glass") shouldBe false
    }

    "glass of water toString names the class and key dimensions" {
        glass().toString() shouldContain "GlassOfWater"
    }
})
