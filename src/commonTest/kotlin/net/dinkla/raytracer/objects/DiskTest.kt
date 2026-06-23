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
class DiskTest : StringSpec({
    // A disk in the y = 0 plane centred at the origin, radius 1.0, facing +y.
    val disk = Disk(Point3D.ORIGIN, 1.0, Normal.UP)

    "disk hit inside the radius records t and the facing normal" {
        // Straight down at radius 0.5 (< 1.0): lands at (0.5, 0, 0), t = 2.0.
        val ray = Ray(Point3D(0.5, 2.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        disk.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBe Normal.UP
    }

    "disk rejects a point outside the radius" {
        // Straight down at radius 1.5 (> 1.0): in the plane but off the disk.
        val ray = Ray(Point3D(1.5, 2.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        disk.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        disk.shadowHit(ray) shouldBe Shadow.None
    }

    "disk rejects a ray whose intersection is behind the origin (t <= epsilon)" {
        // From (0, -2, 0) toward -y: the plane is at t = -2 (behind), so no forward hit.
        val ray = Ray(Point3D(0.0, -2.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        disk.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        disk.shadowHit(ray) shouldBe Shadow.None
    }

    "disk shadowHit reports the forward hit inside the radius" {
        val ray = Ray(Point3D(0.5, 2.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        val shadow = disk.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 2.0
    }

    "disk bounding box contains the disk" {
        val bbox = disk.boundingBox

        bbox.p shouldBe Point3D(-1.0, -1.0, -1.0)
        bbox.q shouldBe Point3D(1.0, 1.0, 1.0)
    }

    "disks with equal fields are equal and share a hashCode" {
        val a = Disk(Point3D.ORIGIN, 1.0, Normal.UP)
        val b = Disk(Point3D.ORIGIN, 1.0, Normal.UP)

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "disks differing in one field are not equal" {
        val base = Disk(Point3D.ORIGIN, 1.0, Normal.UP)

        base shouldNotBe Disk(Point3D(1.0, 0.0, 0.0), 1.0, Normal.UP)
        base shouldNotBe Disk(Point3D.ORIGIN, 2.0, Normal.UP)
        base shouldNotBe Disk(Point3D.ORIGIN, 1.0, Normal.DOWN)
    }

    "disk is not equal to null or to an unrelated type" {
        val base = Disk(Point3D.ORIGIN, 1.0, Normal.UP)

        base.equals(null) shouldBe false
        base.equals("disk") shouldBe false
    }

    "disk toString contains the class name" {
        Disk(Point3D.ORIGIN, 1.0, Normal.UP).toString() shouldContain "Disk"
    }
})
