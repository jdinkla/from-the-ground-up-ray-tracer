package net.dinkla.raytracer.objects.acceleration.kdtree

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.shouldBeApprox

/**
 * Direct, structure-controlled tests for [InnerNode]: an interior node split on the x-axis at x=2,
 * with a unit sphere in each child. Building the node by hand (rather than via a builder) pins the
 * front-to-back descent order, the left/right child selection and the bounding-box reject branch.
 */
private fun leafOf(vararg spheres: Sphere): Leaf = Leaf(spheres.toList())

private val wideBox = BBox(Point3D(-1.0, -10.0, -10.0), Point3D(5.0, 10.0, 10.0))

class InnerNodeTest : StringSpec({

    "left-first descent (origin on the low side) resolves the left child's sphere" {
        val left = leafOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        // origin.x = -10 < split = 2 -> left-first branch
        val ray = Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        node.hit(ray, sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
        sr.t shouldBeApprox 9.5 // front surface of the x=0 sphere reached from x=-10
    }

    "right-first descent (origin on the high side) resolves the right child's sphere" {
        val left = leafOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        // origin.x = 10 >= split = 2 -> right-first branch
        val ray = Ray(Point3D(10.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        node.hit(ray, sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D(4.0, 0.0, 0.0)
        sr.t shouldBeApprox 5.5 // back surface at x=4.5 reached from x=10
    }

    "left-first descent falls through to the right child when the left child misses" {
        // left sphere sits off the ray (y=5), so the low-side descent visits left, misses, then
        // takes the right child, exercising the second branch of the left-first arm.
        val left = leafOf(Sphere(Point3D(0.0, 5.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        val ray = Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        node.hit(ray, sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D(4.0, 0.0, 0.0)
    }

    "right-first descent falls through to the left child when the right child misses" {
        val left = leafOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 5.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        // high-side origin descends right (misses, off at y=5) then resolves the left sphere
        val ray = Ray(Point3D(10.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        node.hit(ray, sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
    }

    "a ray that misses the node's bounding box returns false without descending" {
        val left = leafOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        // y = 100 is far above the box -> bounding-box test fails up front
        val ray = Ray(Point3D(-10.0, 100.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        node.hit(ray, sr).shouldBeFalse()
        sr.geometricObject shouldBe null
    }

    "a ray inside the box that strikes neither child returns false" {
        // both child spheres are pulled off the x-axis, so an on-axis ray crosses the box but hits
        // nothing -> the descent exhausts both children and returns false.
        val left = leafOf(Sphere(Point3D(0.0, 8.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 8.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        val ray = Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        node.hit(ray, sr).shouldBeFalse()
    }

    "size sums the object counts of both children" {
        val left = leafOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5), Sphere(Point3D(4.0, 1.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        node.size() shouldBe 3
    }

    "toString reports the node size and both child sizes" {
        val left = leafOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        val text = node.toString()
        text shouldContain "Node 2"
        text shouldContain "[ 1, 1]"
    }

    "printBBoxes renders the split axis and recurses into the children" {
        val left = leafOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5))
        val right = leafOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5))
        val node = InnerNode(left, right, wideBox, split = 2.0, axis = Axis.X)

        val text = node.printBBoxes(0)
        text shouldContain "X" // the split axis is printed
        text shouldContain "2.0" // the split value
    }

    "hit is an InnerNode and not a leaf for a hand-built interior node" {
        val node = InnerNode(leafOf(), leafOf(), wideBox, split = 2.0, axis = Axis.X)

        node.shouldBeInstanceOf<InnerNode>()
    }
})
