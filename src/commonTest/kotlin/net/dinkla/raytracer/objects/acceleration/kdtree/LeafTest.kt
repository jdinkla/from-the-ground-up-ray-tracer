package net.dinkla.raytracer.objects.acceleration.kdtree

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.shouldBeApprox

/**
 * Tests for [Leaf], the brute-force bucket node of a [KDTree]. A leaf wraps its objects in a
 * [net.dinkla.raytracer.objects.compound.Compound] and resolves rays against all of them.
 */
class LeafTest : StringSpec({

    "a ray crossing the leaf's sphere reports the nearest hit" {
        val leaf = Leaf(listOf(Sphere(Point3D(0.0, 0.0, 0.0), 1.0)))

        val ray = Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        leaf.hit(ray, sr).shouldBeTrue()
        sr.t shouldBeApprox 9.0 // front surface of the unit sphere at x=-1, from x=-10
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
    }

    "a ray that passes the leaf's objects reports no hit" {
        val leaf = Leaf(listOf(Sphere(Point3D(0.0, 0.0, 0.0), 1.0)))

        val ray = Ray(Point3D(-10.0, 50.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        leaf.hit(ray, sr).shouldBeFalse()
    }

    "a leaf with several objects resolves the closest one" {
        val near = Sphere(Point3D(0.0, 0.0, 0.0), 0.5)
        val far = Sphere(Point3D(4.0, 0.0, 0.0), 0.5)
        val leaf = Leaf(listOf(far, near))

        val ray = Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        leaf.hit(ray, sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
        sr.t shouldBeApprox 9.5
    }

    "size reports the number of contained objects" {
        val leaf = Leaf(listOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5), Sphere(Point3D(2.0, 0.0, 0.0), 0.5)))

        leaf.size() shouldBe 2
    }

    "boundingBox encloses the contained sphere" {
        val leaf = Leaf(listOf(Sphere(Point3D(0.0, 0.0, 0.0), 1.0)))

        leaf.boundingBox.p shouldBeApprox Point3D(-1.0, -1.0, -1.0)
        leaf.boundingBox.q shouldBeApprox Point3D(1.0, 1.0, 1.0)
    }

    "toString reports the leaf size" {
        val leaf = Leaf(listOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5)))

        leaf.toString() shouldContain "Leaf(1"
    }

    "printBBoxes renders a dash indented by the requested amount" {
        val leaf = Leaf(listOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5)))

        leaf.printBBoxes(2) shouldBe "  -"
    }
})
